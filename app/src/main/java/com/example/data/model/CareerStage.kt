package com.example.data.model

data class CareerPosition(
    val level: Int,
    val title: String,
    val salaryRange: String,
    val summary: String,
    val primaryDomains: List<String>,
    val coreTools: List<String>,
    val microBadges: List<MicroBadge>,
    val minRequiredXp: Long
)

data class MicroBadge(
    val id: String,
    val title: String,
    val domain: String,
    val criteria: String,
    val iconEmoji: String = "🎖️",
    val requiredXp: Long = 0L,
    val requiredLabs: Int = 0
) {
    val name: String get() = title
    val description: String get() = criteria
}

object CareerRoadmapCatalog {
    val positions: List<CareerPosition>
        get() = CareerDataBank.POSITIONS
}

object CareerDataBank {

    val ESSENTIAL_TOOLS: List<String> = listOf(
        "Wireshark", "Nmap", "Angry IP Scanner", "Netcat", "tcpdump",
        "PuTTY", "Ping", "Traceroute", "Iperf", "OpenSSL",
        "Metasploit", "Burp Suite", "Snort", "Zeek", "Nessus",
        "Aircrack-ng", "Hydra", "John the Ripper", "Wi-Fi Analyzer", "Fiddler",
        "Postman", "Docker", "Git", "Ansible", "Terraform",
        "iptables", "UFW", "dig", "nslookup", "whois"
    )

    val POSITIONS: List<CareerPosition> = listOf(
        CareerPosition(
            level = 1,
            title = "Tier 1 Helpdesk Technician",
            salaryRange = "$45,000 - $58,000",
            summary = "First line of defense resolving endpoint incidents, OS issues, and hardware setup.",
            primaryDomains = listOf("Windows 11", "CompTIA A+", "Peripheral Diagnostics", "Cabling"),
            coreTools = listOf("Ping", "Traceroute", "PuTTY"),
            microBadges = listOf(
                MicroBadge("badge_a_plus_1", "A+ Hardware & BIOS", "CompTIA A+", "Triage hardware, RAM, motherboard & BIOS POST errors", "🖥️", 0L, 0),
                MicroBadge("badge_win_support", "Windows Triage", "Windows", "Master Event Viewer, Task Manager, and registry rollbacks", "🪟", 100L, 1),
                MicroBadge("badge_ticket_master", "Incident Resolution", "Helpdesk", "Resolve and document customer tickets with precision", "📋", 200L, 2)
            ),
            minRequiredXp = 0L
        ),
        CareerPosition(
            level = 2,
            title = "IT Field Support Engineer",
            salaryRange = "$54,000 - $68,000",
            summary = "On-site infrastructure maintenance, patch panel termination, switch rack mounting and mobile triage.",
            primaryDomains = listOf("Termux", "Hardware LAN", "Wi-Fi Analysis", "Linux CLI"),
            coreTools = listOf("Angry IP Scanner", "Wi-Fi Analyzer", "nslookup", "whois"),
            microBadges = listOf(
                MicroBadge("badge_termux_field", "Termux Field Ops", "Termux", "Deploy mobile CLI packet and subnet testing on Android", "📱", 250L, 2),
                MicroBadge("badge_angry_ip", "Subnet Scout", "Tools", "Map live IP subnets and open management ports with Angry IP", "🔍", 400L, 3),
                MicroBadge("badge_wifi_analyzer", "RF Spectrum Analyst", "Wireless", "Diagnose channel interference and signal strength", "📶", 500L, 3)
            ),
            minRequiredXp = 250L
        ),
        CareerPosition(
            level = 3,
            title = "Junior Systems Administrator",
            salaryRange = "$62,000 - $78,000",
            summary = "Domain Controller management, Active Directory OUs, Group Policy, and automated provisioning.",
            primaryDomains = listOf("Windows Server", "Active Directory", "PowerShell", "DNS/DHCP"),
            coreTools = listOf("PowerShell", "nslookup", "PuTTY"),
            microBadges = listOf(
                MicroBadge("badge_ad_gpo", "Active Directory Guardian", "Windows Server", "Provision OUs, security groups, and baseline GPOs", "🏢", 600L, 4),
                MicroBadge("badge_ps_admin", "PowerShell Automator", "PowerShell", "Automate bulk user onboarding and service restarts via scripts", "⚡", 800L, 5),
                MicroBadge("badge_dns_dhcp", "Core IP Service Keeper", "Networking", "Configure DHCP reservations and split-horizon DNS records", "🌐", 1000L, 5)
            ),
            minRequiredXp = 600L
        ),
        CareerPosition(
            level = 4,
            title = "Network Operations Technician (NOC)",
            salaryRange = "$70,000 - $88,000",
            summary = "24/7 telemetry monitoring, circuit flaps, packet captures, and routing anomalies.",
            primaryDomains = listOf("Cisco IOS", "CCNA Fundamentals", "Wireshark", "Syslog"),
            coreTools = listOf("Wireshark", "Netcat", "tcpdump", "Iperf"),
            microBadges = listOf(
                MicroBadge("badge_wireshark_pcap", "Packet Sleuth", "Wireshark", "Isolate TCP resets, SYN floods, and retransmissions in PCAP", "🦈", 1200L, 6),
                MicroBadge("badge_iperf_band", "Throughput Verifier", "Iperf", "Benchmark gigabit MTU, jitter, and packet loss", "📊", 1400L, 6),
                MicroBadge("badge_tcpdump_stream", "CLI Packet Siphon", "Linux", "Filter live promiscuous packet feeds with BPF syntax", "📟", 1600L, 7)
            ),
            minRequiredXp = 1200L
        ),
        CareerPosition(
            level = 5,
            title = "Network Infrastructure Engineer",
            salaryRange = "$85,000 - $110,000",
            summary = "Enterprise VLAN segmentation, 802.1Q trunking, OSPF area routing, and high-availability VRRP/HSRP.",
            primaryDomains = listOf("CCNA Enterprise", "OSPF", "BGP", "Spanning Tree (STP)"),
            coreTools = listOf("Wireshark", "Nmap", "PuTTY", "OpenSSL"),
            microBadges = listOf(
                MicroBadge("badge_ccna_ospf", "OSPF Route Master", "CCNA", "Design multi-area OSPF adjacencies and route convergence", "🔄", 2000L, 8),
                MicroBadge("badge_vlan_trunk", "802.1Q Architect", "Switching", "Isolate corporate, voice, and guest VLANs with Rapid-PVST+", "🔀", 2400L, 8),
                MicroBadge("badge_nmap_auditor", "Port & Service Cartographer", "Nmap", "Stealth SYN scan across CIDR blocks for vulnerability exposure", "🎯", 2800L, 9)
            ),
            minRequiredXp = 2000L
        ),
        CareerPosition(
            level = 6,
            title = "Cybersecurity SOC Analyst",
            salaryRange = "$90,000 - $120,000",
            summary = "Threat hunting, intrusion detection analysis, SIEM event correlation, and firewall ACL enforcement.",
            primaryDomains = listOf("CompTIA Security+", "Threat Hunting", "Snort IDS", "SIEM"),
            coreTools = listOf("Snort", "Zeek", "Nessus", "iptables", "UFW"),
            microBadges = listOf(
                MicroBadge("badge_snort_ids", "Intrusion Sentinel", "Snort", "Craft custom IDS rule patterns against CVE exploit signatures", "🛡️", 3200L, 10),
                MicroBadge("badge_firewall_hard", "Perimeter Bastion", "Security", "Harden stateless and stateful iptables/UFW rulechains", "🧱", 3600L, 10),
                MicroBadge("badge_nessus_scan", "Vulnerability Assessor", "Nessus", "Remediate critical CVSS score exposures across endpoints", "🔬", 4000L, 11)
            ),
            minRequiredXp = 3200L
        ),
        CareerPosition(
            level = 7,
            title = "DevSecOps & NetDevOps Engineer",
            salaryRange = "$110,000 - $145,000",
            summary = "Infrastructure-as-Code, Python Network Automation with Netmiko/Paramiko, and CI/CD pipelines.",
            primaryDomains = listOf("Python NetDevOps", "Docker", "Ansible", "Git CI/CD"),
            coreTools = listOf("Docker", "Git", "Ansible", "Postman"),
            microBadges = listOf(
                MicroBadge("badge_python_netmiko", "Python DevNet Pioneer", "Python", "Push multi-vendor switch configs over SSH concurrently via Netmiko", "🐍", 5000L, 12),
                MicroBadge("badge_ansible_play", "Playbook Architect", "Ansible", "Deploy declarative network state across enterprise fleet", "📜", 5600L, 12),
                MicroBadge("badge_docker_micro", "Containerized Gateway", "Docker", "Containerize reverse proxies and microservice routing planes", "🐳", 6200L, 13)
            ),
            minRequiredXp = 5000L
        ),
        CareerPosition(
            level = 8,
            title = "Senior Infrastructure Engineer",
            salaryRange = "$125,000 - $160,000",
            summary = "High-availability multi-cloud data center fabrics, SD-WAN, zero-trust VPN mesh, and BGP peering.",
            primaryDomains = listOf("BGP Multi-homing", "SD-WAN", "Terraform", "Cloud Networking"),
            coreTools = listOf("Terraform", "OpenSSL", "Zeek", "Fiddler"),
            microBadges = listOf(
                MicroBadge("badge_terraform_iac", "Cloud Fabric Builder", "Terraform", "Provision VPC peering, transit gateways, and route tables as code", "🏗️", 7500L, 14),
                MicroBadge("badge_pki_mesh", "Cryptographic Vanguard", "OpenSSL", "Generate enterprise root CA trust chains and mutual TLS tunnels", "🔐", 8500L, 14),
                MicroBadge("badge_sdwan_edge", "SD-WAN Strategist", "WAN", "Deploy application-aware routing over dual MPLS & DIA circuits", "🌐", 9500L, 15)
            ),
            minRequiredXp = 7500L
        ),
        CareerPosition(
            level = 9,
            title = "Security Architect / Penetration Specialist",
            salaryRange = "$140,000 - $185,000",
            summary = "Red team adversarial simulation, perimeter penetration testing, and zero-trust security architecture.",
            primaryDomains = listOf("Ethical Hacking", "Zero-Trust Architecture", "Reverse Engineering", "Defense-in-Depth"),
            coreTools = listOf("Metasploit", "Burp Suite", "Hydra", "John the Ripper", "Aircrack-ng"),
            microBadges = listOf(
                MicroBadge("badge_metasploit_exploit", "Red Team Infiltrator", "Metasploit", "Audit privilege escalation pathways and simulate attacker lateral movement", "🎯", 10500L, 16),
                MicroBadge("badge_burp_websec", "Application Perimeter Shield", "Burp Suite", "Intercept, decode, and remediate web API vulnerabilities", "🛡️", 11500L, 16),
                MicroBadge("badge_auth_crack", "Credential Hardener", "Hydra", "Stress-test weak hashes and enforce enterprise password entropy", "🔑", 12500L, 17)
            ),
            minRequiredXp = 10500L
        ),
        CareerPosition(
            level = 10,
            title = "Lead Enterprise Architect & Security Vanguard",
            salaryRange = "$175,000 - $240,000+",
            summary = "Executive technical oversight of global enterprise topology, cyber resiliency, and technical talent mentorship.",
            primaryDomains = listOf("Enterprise Strategy", "Global BGP Backbones", "Autonomous NetOps", "Executive Mentorship"),
            coreTools = CareerDataBank.ESSENTIAL_TOOLS.take(15),
            microBadges = listOf(
                MicroBadge("badge_vanguard_zenith", "Zenith Vanguard", "Omni-Domain", "Demonstrated omni-domain mastery across Python, Linux, Windows, & Networks", "👑", 15000L, 18),
                MicroBadge("badge_chief_architect", "Enterprise Laureate", "Leadership", "Architected self-healing enterprise mesh with zero downtime", "🏛️", 17500L, 20),
                MicroBadge("badge_mentor_titan", "Master Mentor", "Academy", "Elevated junior talent across 10 career tiers", "🌟", 20000L, 22)
            ),
            minRequiredXp = 15000L
        )
    )
}
