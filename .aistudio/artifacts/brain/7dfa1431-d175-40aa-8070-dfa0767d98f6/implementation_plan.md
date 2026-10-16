# Network Teach: Architecture Overhaul & Feature Expansion Plan

Comprehensive update and architectural transition plan for **Network Teach** (NetQuest IT). This update elevates the application from 80% to 95% completion by restructuring navigation into a focused 4-tab bottom bar `[Learn | Journal | Portfolio | Dictionary]`, transitioning the Dashboard into a slide-out Hamburger Drawer featuring a tabbed switch between KPI Metrics and the 10-Tier Career Roadmap, introducing an AI/Offline Activity Log tracker with performance-calibrated drills, implementing global inline pencil editing, delivering a 70/30 hybrid flashcard engine with dedicated sub-tab and fullscreen overlay modes, securing strictly offline Room SQLite local persistence, and packaging an Executive Recruiter Ready View with one-tap Markdown dossier export.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> Key architectural decisions confirmed through interactive clarification:

- **Confirmed Decision 1 (Hamburger Drawer Architecture)**: Tabbed switch within the slide-out Hamburger Drawer allowing users to toggle seamlessly between **[KPI Metrics & Activity Punch]** and the **[10-Tier Career Progression & Micro-Badges Roadmap]**.
- **Confirmed Decision 2 (Recruiter Ready View Dossier Format)**: Formatted Markdown dossier with one-tap clipboard copy and Android share sheet intent, compiling case studies, verified lab notes, and mastered commands into an executive format for hiring managers.
- **Confirmed Decision 3 (70/30 Flashcard Experience)**: Hybrid dual-mode presentation: a dedicated sub-tab inside the Learn section for casual card flipping, paired with an on-demand Fullscreen Interactive Flashcard Session overlay for distraction-free drills.
- **Confirmed Decision 4 (Persistence & Air-Gapped Operation)**: Strictly offline local storage utilizing Android Room SQLite. No network connection is required; all dictionary definitions, field notes, case studies, and activity records persist safely across app launches and updates without data loss.
- **Confirmed Decision 5 (Top App Bar Motivational Chips)**: Compact Streak Fire chip (displaying consecutive days and active multiplier, e.g. `🔥 14d (1.70x)`) alongside an XP badge (`⚡ 2,450 XP`) remains permanently visible on every screen.

---

### 1. Overview & Core Concept

- **What It Does**: Network Teach is an offline-first IT engineering companion, journal, command dictionary, and certification preparation suite tailored for field technicians, systems administrators, network engineers, and cybersecurity analysts.
- **Target Audience / Persona**: IT professionals operating both in modern offices and zero-connectivity environments (secure data centers, air-gapped server vaults, enterprise wiring closets, remote field sites).
- **Key Value**: 100% reliable on-device operation, zero risk of data loss on update, effortless inline editing of technical notes, structured progression across 10 industry career milestones, and instant generation of a recruiter-ready technical portfolio.

---

### 2. User Experience & Visual Design

#### Navigation Structure
1. **TopAppBar**:
   - Left: Hamburger Menu Icon (`Icons.Default.Menu`) triggering the slide-out navigation drawer.
   - Center: Active Screen Title (`Learn`, `Journal`, `Portfolio`, `Dictionary`).
   - Right: Compact Streak Chip (`🔥 14d (1.70x)`), XP Badge (`⚡ 2,450 XP`), AI Mentor Shortcut, and PIN Lock trigger.
2. **Bottom Navigation Bar (Strict 4-Tab Layout)**:
   - `[Learn]` (`Icons.Default.School`): Quests, 70/30 Hybrid Flashcards, Activity Log, and Worksheets.
   - `[Journal]` (`Icons.AutoMirrored.Filled.Assignment`): Field troubleshooting logs, CLI scripts, and verified incident notes.
   - `[Portfolio]` (`Icons.Default.Work`): Employer case studies, architecture diagrams, verified metrics, and Recruiter Ready View.
   - `[Dictionary]` (`Icons.Default.Book`): Searchable command library, port lookup, RFC definitions, and mastery tracking.
3. **Slide-Out Hamburger Navigation Drawer**:
   - Drawer Header: Engineer Profile, Current Tier (`Tier 4 - Junior Systems Administrator`), XP Level progress bar, and Streak Multiplier chip.
   - Navigation Tab Row:
     - `[KPI Metrics]`: Readiness audit score, verified lab count, mastered dictionary ratio, case study count, and weekly activity sparkline matrix.
     - `[Career Roadmap]`: Interactive breakdown of all 10 IT positions (from Helpdesk Tier 1 up to Lead Enterprise Architect), required XP thresholds, salary benchmarks, and domain micro-badges.
   - Footer / Action Links: Recruiter Ready View, AI Systems Mentor, and PIN Lock trigger.

#### Visual Identity & Theme
- **Theme Direction**: High-contrast, utilitarian Cyber-Terminal Dark Theme with vivid status indicators (`#00E5FF` Cyan, `#76FF03` Terminal Green, `#FF6D00` Amber Streak Fire, `#A855F7` Purple Category Pills, `#1E293B` Slate Surface Cards).
- **Typography**: Monospaced font families for code blocks, IP addresses, subnets, and CLI syntax, paired with clean sans-serif typography for readable notes and case study summaries.
- **Interactive Animations**: Smooth drawer slide-in, 3D flip card animations (`rotationY` transitions) for flashcards, and instant reactive updates on edit dialogs.

---

### 3. Key Product Decisions & Trade-Offs

#### Decision 1: Tabbed Hamburger Drawer (KPIs vs. Career Roadmap)
- *Chosen Approach*: Implement a dual-tab selector inside the drawer header toggling between KPI metrics and the 10-tier career progression roadmap.
- *Why*: Prevents infinite vertical scrolling while keeping detailed metrics and the comprehensive 10-stage career path easily accessible from any screen in the app.
- *Alternatives Considered*: Single monolithic scrolling drawer. Rejected due to visual clutter and excessive cognitive load.

#### Decision 2: 70/30 Hybrid Flashcard Delivery (Sub-Tab + Fullscreen Overlay)
- *Chosen Approach*: Embed flashcard practice as a sub-tab inside Learn for casual review, with a "Launch Fullscreen Session" button that opens an immersive, distraction-free drill overlay.
- *Why*: Accommodates both quick 1-minute command checks and focused 10-card study drills.
- *Alternatives Considered*: Sub-tab only or overlay only. Combining both satisfies diverse study habits.

#### Decision 3: Progressive Capped Streak Multiplier Formula
- *Chosen Approach*: 
  - Days 1 to 20: $M = 1.0 + (0.05 \times d)$ (reaching $2.0\times$ on Day 20).
  - Day 21 and beyond: $M = 2.0 + 0.01 \times (d - 20)$, safely capped at a maximum ceiling of $2.5\times$ XP.
- *Why*: Delivers high dopamine acceleration during habit formation (first 3 weeks), while capping growth to preserve long-term game balance.

#### Decision 4: Non-Destructive Strictly Local Room Persistence
- *Chosen Approach*: Use Room SQLite with additive schemas and pre-population checks. Existing user logs, terms, and progression are loaded into Room and preserved without wiping or cloud synchronization dependencies.
- *Why*: Meets the critical directive for air-gapped server room operations and guarantees zero data loss during updates.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                              MainActivity                              │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ TopAppBar: [☰ Menu] [Screen Title] [🔥 Streak (Mx)] [⚡ XP Chip] │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                  │                                     │
│     ┌────────────────────────────┼───────────────────────────┐         │
│     ▼                            ▼                           ▼         │
│ ┌────────────────┐       ┌──────────────┐            ┌──────────────┐  │
│ │Hamburger Drawer│       │Screen Content│            │Modals/Sheets │  │
│ │[KPIs] [Career] │       │• Learn       │            │• Edit Term   │  │
│ │• Metrics/Punch │       │• Journal     │            │• Edit Journal│  │
│ │• 10 Positions  │       │• Portfolio   │            │• Edit Case   │  │
│ │• Micro-Badges  │       │• Dictionary  │            │• Recruiter   │  │
│ └────────────────┘       └──────────────┘            └──────────────┘  │
│                                  ▲                                     │
│  ┌───────────────────────────────┴──────────────────────────────────┐  │
│  │ BottomBar: [ Learn ] | [ Journal ] | [ Portfolio ] | [ Dictionary│  │
│  └──────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                          MainViewModel                                 │
│  • observeStreakMultiplier() • observeActivityLogs() • editItem()      │
│  • generateHybridDeck()      • calibrateDrillsFromWeakSpots()          │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                       NetQuestRepository                               │
│  ┌───────────────────────────────────┬──────────────────────────────┐  │
│  │      Room SQLite Database         │  Offline IT Knowledge Bank   │  │
│  │ • DictionaryDao   • JournalDao    │ • 30+ IT & Security Tools    │  │
│  │ • PortfolioDao    • QuestDao      │ • 10 Career Positions        │  │
│  │ • UserProfileDao  • ActivityLogDao│ • Pre-seeded Engineering Labs│  │
│  └───────────────────────────────────┴──────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

#### Core Data Entities

1. **`ActivityLog`**:
   - `id: String`, `title: String`, `moduleType: String` ("QUIZ", "WORKSHEET", "FLASHCARDS", "TEST", "PROJECT")
   - `status: String` ("Completed", "Incomplete")
   - `gradePercent: Int?`, `completedSteps: Int`, `totalSteps: Int`, `weakTopics: List<String>`, `summary: String`, `timestamp: Long`
   - Formatted label: `"Completed (90%)"` or `"Incomplete (4/10 steps)"`

2. **10 Distinct Career Positions**:
   1. Tier 1 Helpdesk Technician (A+ hardware, triage)
   2. IT Field Support Engineer (Termux, Angry IP, LAN cabling)
   3. NOC Technician Tier 1 (Wireshark PCAP, tcpdump, Iperf, latency)
   4. Junior Systems Administrator (PowerShell, AD, Linux systemd)
   5. Network Specialist / Associate (CCNA, VLANs, OSPF, VLSM)
   6. Cybersecurity Analyst SOC Tier 1 (Snort IDS, Zeek, Nessus)
   7. Systems & NetDevOps Automation Engineer (Python Netmiko, Ansible, APIs)
   8. Senior Infrastructure Engineer (BGP, SD-WAN, Terraform, PKI)
   9. Security Architect / Penetration Specialist (Metasploit, Burp Suite, red teaming)
   10. Lead Enterprise Architect & Security Vanguard (Autonomous NetOps, global mesh)

3. **Inline Edit Modals (Pencil Icon Integration)**:
   - Dictionary: pencil edit button opening `EditDictionaryDialog` pre-filled with command, description, and difficulty.
   - Journal: pencil edit button opening `EditJournalDialog` pre-filled with title, notes, solution, and tags.
   - Portfolio: pencil edit button opening `EditPortfolioDialog` pre-filled with summary, problem, tools, steps, and metrics.

4. **Recruiter Ready View**:
   - Activated via the eye icon (`Icons.Default.Visibility`) in the Portfolio screen or Hamburger Drawer.
   - Compiles executive markdown profile, top case studies, and verified lab solutions with a one-tap copy and share button.

---

### Step-by-Step Implementation Sequence

1. **Finalize Local Storage & Repository Wiring**:
   - Complete Room DAOs, entities, and database wiring in `NetQuestRepository` for ActivityLog, Dictionary, Journal, Portfolio, and UserProfile.
   - Verify non-destructive initialization logic.
2. **Update Hamburger Navigation Drawer**:
   - Enhance `HamburgerDashboardDrawer.kt` with a tabbed switch between `[KPI Metrics]` and `[Career Roadmap]`.
   - Render the 10-tier position breakdown, salary bands, and micro-badges.
3. **Refine Learn Section & 70/30 Flashcards**:
   - Complete `LearnScreen.kt` with curriculum, dedicated 70/30 card flip sub-tab, on-demand fullscreen session overlay, and Activity Log with calibration triggers.
4. **Complete Inline Editing Across All Screens**:
   - Wire pencil icons in `JournalScreen.kt` and `PortfolioScreen.kt` with `EditJournalDialog` and `EditPortfolioDialog`.
   - Verify `DictionaryScreen.kt` pencil icon integration.
5. **Connect MainActivity Navigation**:
   - Reconfigure bottom navigation bar strictly to 4 items: `[ Learn | Journal | Portfolio | Dictionary ]`.
   - Set up `ModalNavigationDrawer` triggered by the top-left hamburger menu.
   - Place compact streak multiplier and XP chips in `TopAppBar`.
6. **Recruiter Ready View Export Wiring**:
   - Connect the eye icon in `PortfolioScreen.kt` and Drawer to launch `RecruiterReadyViewModal.kt`.
7. **Verification & Build**:
   - Run compilation and Robolectric/unit test suite via `compile_applet` and `run_command` (`gradle :app:testDebugUnitTest`).
