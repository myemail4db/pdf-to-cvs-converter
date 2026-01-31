# PDF to CSV Converter (Spring Boot + PDFBox)

A Spring Boot application that extracts AcroForm fields from PDF documents and converts them into a clean CSV file.

This project demonstrates:
- Java 21
- Spring Boot 3.4.x
- PDFBox for form extraction
- REST API endpoints
- Simple static UI for instant click-to-convert
- 100% JUnit test coverage

Perfect for backend portfolio demonstration.

---

# Features

## Convert PDF → CSV
Extracts all AcroForm fields and generates a clean CSV file.

## Directory-based PDF loading
The app scans a local directory automatically for .pdf files.

## Option 3: Clickable UI — Select & Convert Instantly

Your homepage (index.html) now shows:
- A list of PDFs from the configured directory
- Each item is clickable
- Clicking automatically downloads the CSV version
- No buttons, no forms

A great UX for demos and portfolios.

## REST Endpoints
- GET /api/dir/pdfs – List PDFs in configured directory
- GET /api/dir/pdf-to-csv?fileName= – Convert one PDF
- POST /api/upload – (Optional) Upload a PDF to convert

## Test Coverage
Comprehensive tests covering:
- Service logic
- Controller logic
- Error handling
- CSV escaping
- Real PDFs generated with PDFBox
- MockMVC endpoint testing
- Full context load

---

# Configuration
Edit your input directory in:

```css
src/main/resources/application.properties
```

Example:

```properties
pdf.input.dir=C:/my-pdf-folder
```

Place your PDFs inside this folder.

---

# Click-to-Convert Web UI
This app includes a simple static UI that loads automatically at:

```arduino
http://localhost:8080/
```

## How it works

1. The frontend calls:

```bash
GET /api/dir/pdfs
```

to fetch all available PDFs.

2. The UI builds a clickable list of those files.
3. When a user clicks a filename, it triggers:

```bash
GET /api/dir/pdf-to-csv?fileName=example.pdf
```
which immediately downloads the CSV file.

## User Experience
- No buttons
- No page reloads
- No form submissions
- Just click → convert → download
Simple, clean, intuitive.

---

# Testing
Run:

```
mvn clean verify
```

Tests include:
- PDF generation for realistic validation
- CSV escaping
- Directory listing
- Error branches (missing file, missing directory, service failure)
- MockMvc endpoint tests
- Reflection-based controller tests
- Full Spring context startup

---

# Run the Application

```bash
mvn spring-boot:run
```

Then open:

```arduino
http://localhost:8080/
```

You’ll see your clickable PDF list.

---

# Build JAR

```bash
mvn clean package
```

Run:

```bash
java -jar target/pdf-to-cvs-converter-0.0.1-SNAPSHOT.jar
```

---

# Technologies Used
- Java 21
- Spring Boot 3
- PDFBox (2.x)
- Maven
- MockMvc & JUnit 5
- HTML/JS static UI
