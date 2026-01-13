# 📝 PDF Form to CSV Converter  
### Spring Boot • Java 21 • PDFBox • REST API  
![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-6DB33F?logo=springboot)
![Maven](https://img.shields.io/badge/Maven-3.9.12-C71A36?logo=apachemaven)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](./LICENSE)
![Last Commit](https://img.shields.io/github/last-commit/myemail4db/pdf-to-cvs-converter)
[![Build](https://github.com/myemail4db/pdf-to-cvs-converter/actions/workflows/maven.yml/badge.svg)](https://github.com/myemail4db/pdf-to-cvs-converter/actions/workflows/maven.yml)
[![JavaDoc](https://img.shields.io/badge/JavaDoc-Online-blue)](./docs/javadoc/index.html)
![Author](https://img.shields.io/badge/Author-Dominick%20Benigno-0d6efd)

A Spring Boot application that extracts **fillable PDF form fields** (AcroForms) from PDFs stored in a directory and converts them to **clean, ready-to-use CSV files**.

This project demonstrates:

- Java 21 development  
- Spring Boot REST APIs  
- File processing services  
- Use of Apache PDFBox  
- Clean code, documentation, and API design  

Perfect for portfolios, showcasing backend engineering, and demonstrating real-world document automation.

---

## 🚀 Features

- 📂 Reads PDFs from a configurable folder  
- 🔍 Lists all available PDF files  
- 📤 Converts a selected PDF to CSV  
- 🧾 Extracts *only* editable PDF form fields (AcroForms)  
- 📄 CSV contains field names + user input values  
- 🌱 Built with Spring Boot 3.4.2  
- 🔒 Java 21 compatible  
- 🔧 Maven 3.9.12  

---

## 📌 Technologies Used

| Tech | Version | Purpose |
|------|---------|---------|
| **Java** | 21 | Modern LTS language support |
| **Spring Boot** | 3.4.2 | REST API + configuration |
| **Maven** | 3.9.12 | Build + dependency management |
| **PDFBox** | 2.0.30 | PDF form parsing |
| **Spring Web** | — | Controllers / endpoints |

---

## 📂 How It Works

1. Drop fillable PDFs into your input folder  
2. Hit `/api/dir/pdfs` to list them  
3. Choose a file and call:  

```shell
GET /api/dir/pdf-to-csv?fileName=example.pdf
```

4. A CSV file downloads:

```
FirstName,LastName,Email
"Dominick","Benigno","dominick@example.com"
```

---

## ⚙️ Configuration

Set your input folder in `src/main/resources/application.properties`:

```properties
pdf.input.dir=C:/pdf-input
```

Ensure the folder exists before running.
🏃 Running the App

```bash
mvn spring-boot:run
```

Visit:

```
http://localhost:8080/api/dir/pdfs
```

📬 API Endpoints
🔹 List all PDFs:

```bash
GET /api/dir/pdfs
```

🔹 Convert selected PDF to CSV:

```bash
GET /api/dir/pdf-to-csv?fileName=somefile.pdf
```

📁 Example Repository Layout

```swift
src/main/java/com/example/pdftocsv/
│
├── PdfToCsvApplication.java
├── controller/
│   └── PdfDirectoryController.java
└── service/
    └── PdfToCsvService.java
```

