package com.example.data.service

data class ToolFlashcardQuestion(
    val toolName: String,
    val domain: String,
    val question: String,
    val answer: String,
    val commandExample: String = ""
)

object OfflineToolQuestions {
    val REINFORCEMENT_QUESTIONS: List<ToolFlashcardQuestion> = listOf(
        ToolFlashcardQuestion(
            toolName = "Wireshark",
            domain = "Network Analysis",
            question = "Which Wireshark display filter isolates TCP reset packets used to troubleshoot abrupt connection teardowns?",
            answer = "tcp.flags.reset == 1 (or tcp.flags.rst == 1)",
            commandExample = "tcp.flags.reset == 1"
        ),
        ToolFlashcardQuestion(
            toolName = "Nmap",
            domain = "Port Scanning & Recon",
            question = "What is the Nmap command flag to perform a SYN Stealth scan without completing the 3-way TCP handshake?",
            answer = "-sS (e.g. nmap -sS 192.168.1.0/24). Requires root/admin privileges.",
            commandExample = "nmap -sS -p- 192.168.1.1"
        ),
        ToolFlashcardQuestion(
            toolName = "Angry IP Scanner",
            domain = "Subnet Discovery",
            question = "How does Angry IP Scanner rapidly detect active hosts across a /24 subnet?",
            answer = "It sends multi-threaded ICMP echo requests and parallel TCP port probes to each IP address simultaneously.",
            commandExample = "IP Range: 192.168.1.1 to 192.168.1.254"
        ),
        ToolFlashcardQuestion(
            toolName = "tcpdump",
            domain = "Linux CLI Packet Capture",
            question = "Which tcpdump syntax captures traffic on interface eth0 and writes raw packets to a capture.pcap file?",
            answer = "tcpdump -i eth0 -w capture.pcap",
            commandExample = "tcpdump -i eth0 -nn -s0 -w /tmp/traffic.pcap"
        ),
        ToolFlashcardQuestion(
            toolName = "Netcat",
            domain = "Utility / Pentest",
            question = "How can you use Netcat to test if remote port 443 is open without a full browser handshake?",
            answer = "nc -zv <target_ip> 443",
            commandExample = "nc -zv 10.0.0.1 443"
        ),
        ToolFlashcardQuestion(
            toolName = "Iperf",
            domain = "Throughput Testing",
            question = "What command tests raw TCP bandwidth between an Iperf server and client?",
            answer = "Server: iperf3 -s | Client: iperf3 -c <server_ip> -t 30",
            commandExample = "iperf3 -c 192.168.1.50 -P 4"
        ),
        ToolFlashcardQuestion(
            toolName = "OpenSSL",
            domain = "PKI & Encryption",
            question = "How do you inspect the expiration date and Subject Alternative Names (SAN) of an active TLS cert on port 443?",
            answer = "openssl s_client -connect target.com:443 -servername target.com | openssl x509 -noout -text",
            commandExample = "openssl s_client -connect example.com:443"
        ),
        ToolFlashcardQuestion(
            toolName = "Termux",
            domain = "Mobile Terminal Ops",
            question = "Which package manager command installs python and nmap in an Android Termux environment?",
            answer = "pkg install python nmap -y",
            commandExample = "pkg update && pkg install -y git python nmap"
        ),
        ToolFlashcardQuestion(
            toolName = "PowerShell",
            domain = "Windows Administration",
            question = "Which PowerShell cmdlet tests TCP network port connectivity with response latency?",
            answer = "Test-NetConnection -ComputerName <host> -Port <port>",
            commandExample = "Test-NetConnection -ComputerName dc01 -Port 389"
        ),
        ToolFlashcardQuestion(
            toolName = "Snort",
            domain = "Intrusion Detection",
            question = "What are the three main components of a Snort rule header?",
            answer = "Action (alert/log/pass), Protocol (tcp/udp/icmp/ip), and Source/Destination IP and Port specifiers.",
            commandExample = "alert tcp any any -> 192.168.1.0/24 80 (msg:\"HTTP Probe\"; sid:100001;)"
        ),
        ToolFlashcardQuestion(
            toolName = "Zeek",
            domain = "Network Security Monitoring",
            question = "How does Zeek differ from traditional signature-based packet capture tools?",
            answer = "Zeek performs behavioural semantic protocol analysis and produces structured transaction logs (conn.log, dns.log, ssl.log).",
            commandExample = "zeek -r capture.pcap"
        ),
        ToolFlashcardQuestion(
            toolName = "Subnetting / VLSM",
            domain = "CCNA Routing",
            question = "How many usable host IP addresses are provided by a /27 subnet mask (255.255.255.224)?",
            answer = "30 usable hosts ($2^5 - 2 = 32 - 2 = 30$).",
            commandExample = "Prefix: /27 (Block size: 32)"
        ),
        ToolFlashcardQuestion(
            toolName = "Python DevNet",
            domain = "Network Automation",
            question = "Which Python library provides multi-vendor SSH connection handling and CLI command execution for Cisco/Juniper devices?",
            answer = "Netmiko (built on top of Paramiko).",
            commandExample = "from netmiko import ConnectHandler"
        ),
        ToolFlashcardQuestion(
            toolName = "Nessus",
            domain = "Vulnerability Management",
            question = "What is the standard severity classification scale used by Nessus vulnerability reports?",
            answer = "Critical, High, Medium, Low, and Informational (aligned with CVSS v3.1 scoring).",
            commandExample = "CVSS v3.1 Score: 9.8 (Critical)"
        ),
        ToolFlashcardQuestion(
            toolName = "Burp Suite",
            domain = "Web Application Security",
            question = "In Burp Suite, what component allows real-time manual interception and tampering of HTTP requests and responses?",
            answer = "Burp Proxy (Intercept tab).",
            commandExample = "Proxy Listener on 127.0.0.1:8080"
        )
    )
}
