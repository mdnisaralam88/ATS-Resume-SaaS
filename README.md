# ResumeIQ — AI ATS Resume Analyzer

ResumeIQ is a professional SaaS platform that uses AI to analyze resumes against job descriptions, helping candidates optimize their content for Applicant Tracking Systems.

## 🚀 Quick Start (with Docker)
```bash
docker-compose up --build
```
Access at `http://localhost`.

## 🛠️ Local Development (without Docker)

### Backend
1. Create MySQL DB: `CREATE DATABASE resumeiq;`
2. Configure `backend/src/main/resources/application.yml` with your DB credentials.
3. Run:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

### Frontend
1. Install dependencies:
   ```bash
   cd frontend
   npm install
   ```
2. Run development server:
   ```bash
   npm run dev
   ```
Access at `http://localhost:5173`.

## 🛡️ Default Credentials
- **Admin**: `admin@resumeiq.com` / `Admin@123`
- **Demo User**: `demo@resumeiq.com` / `User@1234`

## 📄 Documentation
For detailed architectural info and feature walkthroughs, see the `walkthrough.md` in the app data directory.
