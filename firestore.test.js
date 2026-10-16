const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read dictionary or profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("dictionary").get());
});

test("Authenticated user: cannot read another user's dictionary or journal", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("dictionary").doc("term1").set({
      userId: ALICE_UID,
      term: "ping",
      category: "Networking",
      difficulty: "Beginner",
      isMastered: false,
      isAiGenerated: false,
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("dictionary").doc("term1").get());
});

test("Authenticated user: can read and write their own dictionary", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("dictionary").doc("term1").set({
      userId: ALICE_UID,
      term: "traceroute",
      command: "traceroute 8.8.8.8",
      description: "Trace route packets to target IP",
      category: "Networking",
      difficulty: "Beginner",
      isMastered: false,
      isAiGenerated: false,
      createdAt: new Date(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("dictionary").doc("term1").get()
  );
});

test("Authenticated user: can create and read journal entries", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("journal").doc("lab1").set({
      userId: ALICE_UID,
      title: "VLAN Configuration Lab",
      notes: "Configured trunk port on Cisco Catalyst switch",
      solution: "switchport mode trunk; switchport trunk allowed vlan 10,20",
      tags: ["cisco", "vlan", "switching"],
      category: "Networking",
      isVerified: true,
      createdAt: new Date(),
    })
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("journal").doc("lab1").get()
  );
});
