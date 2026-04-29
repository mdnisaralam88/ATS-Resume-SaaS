-- =====================================================================
-- ResumeIQ Seed Data
-- Admin user + 6 job roles with full keyword libraries
-- =====================================================================

-- Admin user (password: Admin@123)
INSERT IGNORE INTO users (full_name, email, password, role, subscription_plan, email_verified, active, created_at, updated_at)
VALUES ('ResumeIQ Admin', 'admin@resumeiq.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMeSSqRFvF0A0fYSmV1t6Y5pxC',
        'ADMIN', 'PREMIUM', true, true, NOW(), NOW());

-- Demo user (password: User@1234)
INSERT IGNORE INTO users (full_name, email, password, role, subscription_plan, email_verified, active, created_at, updated_at)
VALUES ('Demo User', 'demo@resumeiq.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMeSSqRFvF0A0fYSmV1t6Y5pxC',
        'USER', 'PRO', true, true, NOW(), NOW());

-- ─── Job Roles ──────────────────────────────────────────────────────

INSERT IGNORE INTO job_roles (name, slug, description, experience_level, category, icon_name, is_active, min_experience_years, max_experience_years, created_at, updated_at)
VALUES
('Java Developer',       'java-developer',       'Backend development with Java ecosystem', 'Mid',    'Backend',  'Coffee',       true, 1, 8,  NOW(), NOW()),
('Full Stack Developer', 'full-stack-developer',  'End-to-end web application development',  'Mid',    'Fullstack', 'Layers',      true, 2, 8,  NOW(), NOW()),
('Backend Developer',   'backend-developer',     'Server-side API and systems development',  'Mid',    'Backend',  'Server',       true, 1, 7,  NOW(), NOW()),
('Frontend Developer',  'frontend-developer',    'UI/UX and client-side development',        'Junior', 'Frontend', 'Monitor',      true, 0, 5,  NOW(), NOW()),
('Software Engineer',   'software-engineer',     'General software development role',        'Mid',    'General',  'Code',         true, 2, 10, NOW(), NOW()),
('DevOps Engineer',     'devops-engineer',       'CI/CD, cloud infrastructure, automation',  'Senior', 'DevOps',   'GitBranch',    true, 3, 10, NOW(), NOW());

-- ─── Java Developer Keywords ─────────────────────────────────────────

INSERT IGNORE INTO role_keywords (job_role_id, keyword, type, weight, category)
SELECT id, keyword, type, weight, category FROM (
  SELECT 'Java'             k, 'REQUIRED', 3, 'Language'   UNION ALL
  SELECT 'Spring Boot'      k, 'REQUIRED', 3, 'Framework'  UNION ALL
  SELECT 'Spring MVC'       k, 'REQUIRED', 2, 'Framework'  UNION ALL
  SELECT 'Spring Security'  k, 'REQUIRED', 2, 'Framework'  UNION ALL
  SELECT 'Hibernate'        k, 'REQUIRED', 2, 'ORM'        UNION ALL
  SELECT 'JPA'              k, 'REQUIRED', 2, 'ORM'        UNION ALL
  SELECT 'REST API'         k, 'REQUIRED', 2, 'Concept'    UNION ALL
  SELECT 'Maven'            k, 'REQUIRED', 1, 'Tool'       UNION ALL
  SELECT 'Microservices'    k, 'PREFERRED', 2, 'Architecture' UNION ALL
  SELECT 'MySQL'            k, 'PREFERRED', 2, 'Database'  UNION ALL
  SELECT 'PostgreSQL'       k, 'PREFERRED', 1, 'Database'  UNION ALL
  SELECT 'JUnit'            k, 'PREFERRED', 1, 'Testing'   UNION ALL
  SELECT 'Mockito'          k, 'PREFERRED', 1, 'Testing'   UNION ALL
  SELECT 'Docker'           k, 'TOOL', 2, 'DevOps'         UNION ALL
  SELECT 'Git'              k, 'TOOL', 1, 'Version Control' UNION ALL
  SELECT 'Kafka'            k, 'PREFERRED', 2, 'Messaging' UNION ALL
  SELECT 'Redis'            k, 'PREFERRED', 1, 'Cache'     UNION ALL
  SELECT 'AWS'              k, 'TOOL', 1, 'Cloud'          UNION ALL
  SELECT 'Lombok'           k, 'TOOL', 1, 'Library'        UNION ALL
  SELECT 'Swagger'          k, 'TOOL', 1, 'Documentation'
) AS tmp(keyword, type, weight, category)
JOIN job_roles ON job_roles.slug = 'java-developer';

-- ─── Full Stack Developer Keywords ──────────────────────────────────

INSERT IGNORE INTO role_keywords (job_role_id, keyword, type, weight, category)
SELECT id, keyword, type, weight, category FROM (
  SELECT 'React'            k, 'REQUIRED', 3, 'Framework'  UNION ALL
  SELECT 'JavaScript'       k, 'REQUIRED', 3, 'Language'   UNION ALL
  SELECT 'TypeScript'       k, 'PREFERRED', 2, 'Language'  UNION ALL
  SELECT 'Node.js'          k, 'REQUIRED', 2, 'Runtime'    UNION ALL
  SELECT 'Spring Boot'      k, 'REQUIRED', 2, 'Framework'  UNION ALL
  SELECT 'Java'             k, 'REQUIRED', 2, 'Language'   UNION ALL
  SELECT 'REST API'         k, 'REQUIRED', 2, 'Concept'    UNION ALL
  SELECT 'HTML'             k, 'REQUIRED', 1, 'Markup'     UNION ALL
  SELECT 'CSS'              k, 'REQUIRED', 1, 'Styling'    UNION ALL
  SELECT 'MongoDB'          k, 'PREFERRED', 2, 'Database'  UNION ALL
  SELECT 'MySQL'            k, 'PREFERRED', 1, 'Database'  UNION ALL
  SELECT 'Docker'           k, 'TOOL', 2, 'DevOps'         UNION ALL
  SELECT 'Git'              k, 'TOOL', 1, 'Version Control' UNION ALL
  SELECT 'Redux'            k, 'PREFERRED', 1, 'State Management' UNION ALL
  SELECT 'GraphQL'          k, 'PREFERRED', 1, 'API'       UNION ALL
  SELECT 'AWS'              k, 'TOOL', 1, 'Cloud'          UNION ALL
  SELECT 'Postman'          k, 'TOOL', 1, 'Testing'        UNION ALL
  SELECT 'Webpack'          k, 'TOOL', 1, 'Build Tool'     UNION ALL
  SELECT 'Tailwind CSS'     k, 'PREFERRED', 1, 'Styling'   UNION ALL
  SELECT 'Next.js'          k, 'PREFERRED', 2, 'Framework'
) AS tmp(keyword, type, weight, category)
JOIN job_roles ON job_roles.slug = 'full-stack-developer';

-- ─── Backend Developer Keywords ──────────────────────────────────────

INSERT IGNORE INTO role_keywords (job_role_id, keyword, type, weight, category)
SELECT id, keyword, type, weight, category FROM (
  SELECT 'REST API'         k, 'REQUIRED', 3, 'Concept'    UNION ALL
  SELECT 'SQL'              k, 'REQUIRED', 2, 'Database'   UNION ALL
  SELECT 'MySQL'            k, 'REQUIRED', 2, 'Database'   UNION ALL
  SELECT 'PostgreSQL'       k, 'PREFERRED', 2, 'Database'  UNION ALL
  SELECT 'Java'             k, 'PREFERRED', 2, 'Language'  UNION ALL
  SELECT 'Python'           k, 'PREFERRED', 2, 'Language'  UNION ALL
  SELECT 'Microservices'    k, 'REQUIRED', 2, 'Architecture' UNION ALL
  SELECT 'Docker'           k, 'TOOL', 2, 'DevOps'         UNION ALL
  SELECT 'Kubernetes'       k, 'PREFERRED', 2, 'DevOps'    UNION ALL
  SELECT 'Redis'            k, 'PREFERRED', 2, 'Cache'     UNION ALL
  SELECT 'Kafka'            k, 'PREFERRED', 2, 'Messaging' UNION ALL
  SELECT 'Spring Boot'      k, 'PREFERRED', 2, 'Framework' UNION ALL
  SELECT 'Authentication'   k, 'REQUIRED', 1, 'Security'   UNION ALL
  SELECT 'JWT'              k, 'REQUIRED', 1, 'Security'   UNION ALL
  SELECT 'Git'              k, 'TOOL', 1, 'Version Control' UNION ALL
  SELECT 'CI/CD'            k, 'PREFERRED', 1, 'DevOps'    UNION ALL
  SELECT 'AWS'              k, 'TOOL', 1, 'Cloud'          UNION ALL
  SELECT 'MongoDB'          k, 'PREFERRED', 1, 'Database'  UNION ALL
  SELECT 'Unit Testing'     k, 'PREFERRED', 1, 'Testing'   UNION ALL
  SELECT 'Hibernate'        k, 'PREFERRED', 1, 'ORM'
) AS tmp(keyword, type, weight, category)
JOIN job_roles ON job_roles.slug = 'backend-developer';

-- ─── Frontend Developer Keywords ─────────────────────────────────────

INSERT IGNORE INTO role_keywords (job_role_id, keyword, type, weight, category)
SELECT id, keyword, type, weight, category FROM (
  SELECT 'React'            k, 'REQUIRED', 3, 'Framework'  UNION ALL
  SELECT 'JavaScript'       k, 'REQUIRED', 3, 'Language'   UNION ALL
  SELECT 'TypeScript'       k, 'REQUIRED', 2, 'Language'   UNION ALL
  SELECT 'HTML'             k, 'REQUIRED', 2, 'Markup'     UNION ALL
  SELECT 'CSS'              k, 'REQUIRED', 2, 'Styling'    UNION ALL
  SELECT 'Responsive Design' k, 'REQUIRED', 2, 'Concept'  UNION ALL
  SELECT 'Redux'            k, 'PREFERRED', 2, 'State'     UNION ALL
  SELECT 'Next.js'          k, 'PREFERRED', 2, 'Framework' UNION ALL
  SELECT 'Tailwind CSS'     k, 'PREFERRED', 1, 'Styling'   UNION ALL
  SELECT 'SASS'             k, 'PREFERRED', 1, 'Styling'   UNION ALL
  SELECT 'REST API'         k, 'REQUIRED', 1, 'Integration' UNION ALL
  SELECT 'Webpack'          k, 'TOOL', 1, 'Build'          UNION ALL
  SELECT 'Vite'             k, 'TOOL', 1, 'Build'          UNION ALL
  SELECT 'Git'              k, 'TOOL', 1, 'Version Control' UNION ALL
  SELECT 'Jest'             k, 'PREFERRED', 1, 'Testing'   UNION ALL
  SELECT 'Figma'            k, 'TOOL', 1, 'Design'         UNION ALL
  SELECT 'Accessibility'    k, 'PREFERRED', 1, 'Concept'   UNION ALL
  SELECT 'Performance Optimization' k, 'PREFERRED', 1, 'Concept' UNION ALL
  SELECT 'GraphQL'          k, 'PREFERRED', 1, 'API'       UNION ALL
  SELECT 'React Native'     k, 'PREFERRED', 1, 'Mobile'
) AS tmp(keyword, type, weight, category)
JOIN job_roles ON job_roles.slug = 'frontend-developer';

-- ─── Software Engineer Keywords ──────────────────────────────────────

INSERT IGNORE INTO role_keywords (job_role_id, keyword, type, weight, category)
SELECT id, keyword, type, weight, category FROM (
  SELECT 'Data Structures'  k, 'REQUIRED', 3, 'CS Fundamentals' UNION ALL
  SELECT 'Algorithms'       k, 'REQUIRED', 3, 'CS Fundamentals' UNION ALL
  SELECT 'Object Oriented Programming' k, 'REQUIRED', 2, 'Concept' UNION ALL
  SELECT 'Java'             k, 'PREFERRED', 2, 'Language'  UNION ALL
  SELECT 'Python'           k, 'PREFERRED', 2, 'Language'  UNION ALL
  SELECT 'System Design'    k, 'REQUIRED', 2, 'Architecture' UNION ALL
  SELECT 'SQL'              k, 'REQUIRED', 2, 'Database'   UNION ALL
  SELECT 'Git'              k, 'TOOL', 1, 'Version Control' UNION ALL
  SELECT 'Design Patterns'  k, 'PREFERRED', 2, 'Architecture' UNION ALL
  SELECT 'Agile'            k, 'PREFERRED', 1, 'Methodology' UNION ALL
  SELECT 'SOLID Principles' k, 'PREFERRED', 1, 'Concept'   UNION ALL
  SELECT 'REST API'         k, 'PREFERRED', 1, 'Concept'   UNION ALL
  SELECT 'Testing'          k, 'REQUIRED', 1, 'Quality'    UNION ALL
  SELECT 'Docker'           k, 'TOOL', 1, 'DevOps'         UNION ALL
  SELECT 'Linux'            k, 'PREFERRED', 1, 'OS'        UNION ALL
  SELECT 'Cloud'            k, 'PREFERRED', 1, 'Platform'  UNION ALL
  SELECT 'CI/CD'            k, 'PREFERRED', 1, 'DevOps'    UNION ALL
  SELECT 'Code Review'      k, 'PREFERRED', 1, 'Process'   UNION ALL
  SELECT 'Microservices'    k, 'PREFERRED', 1, 'Architecture' UNION ALL
  SELECT 'Problem Solving'  k, 'REQUIRED', 2, 'Skill'
) AS tmp(keyword, type, weight, category)
JOIN job_roles ON job_roles.slug = 'software-engineer';

-- ─── DevOps Engineer Keywords ────────────────────────────────────────

INSERT IGNORE INTO role_keywords (job_role_id, keyword, type, weight, category)
SELECT id, keyword, type, weight, category FROM (
  SELECT 'Docker'           k, 'REQUIRED', 3, 'Container'  UNION ALL
  SELECT 'Kubernetes'       k, 'REQUIRED', 3, 'Orchestration' UNION ALL
  SELECT 'CI/CD'            k, 'REQUIRED', 3, 'Pipeline'   UNION ALL
  SELECT 'Jenkins'          k, 'PREFERRED', 2, 'CI/CD'     UNION ALL
  SELECT 'GitHub Actions'   k, 'PREFERRED', 2, 'CI/CD'     UNION ALL
  SELECT 'AWS'              k, 'REQUIRED', 3, 'Cloud'      UNION ALL
  SELECT 'Terraform'        k, 'REQUIRED', 2, 'IaC'        UNION ALL
  SELECT 'Ansible'          k, 'PREFERRED', 2, 'Automation' UNION ALL
  SELECT 'Linux'            k, 'REQUIRED', 2, 'OS'         UNION ALL
  SELECT 'Bash'             k, 'REQUIRED', 2, 'Scripting'  UNION ALL
  SELECT 'Python'           k, 'PREFERRED', 1, 'Language'  UNION ALL
  SELECT 'Prometheus'       k, 'PREFERRED', 2, 'Monitoring' UNION ALL
  SELECT 'Grafana'          k, 'PREFERRED', 2, 'Monitoring' UNION ALL
  SELECT 'ELK Stack'        k, 'PREFERRED', 1, 'Logging'   UNION ALL
  SELECT 'Helm'             k, 'TOOL', 2, 'Kubernetes'     UNION ALL
  SELECT 'Git'              k, 'TOOL', 1, 'Version Control' UNION ALL
  SELECT 'Nginx'            k, 'TOOL', 1, 'Web Server'     UNION ALL
  SELECT 'Azure'            k, 'PREFERRED', 1, 'Cloud'     UNION ALL
  SELECT 'GCP'              k, 'PREFERRED', 1, 'Cloud'     UNION ALL
  SELECT 'Security'         k, 'PREFERRED', 1, 'DevSecOps'
) AS tmp(keyword, type, weight, category)
JOIN job_roles ON job_roles.slug = 'devops-engineer';

-- ─── Default subscriptions for seeded users ──────────────────────────

INSERT IGNORE INTO subscriptions (user_id, plan, status, scans_used_today, total_scans_used, scans_limit, start_date, renewal_date, created_at, updated_at)
SELECT id, 'PREMIUM', 'ACTIVE', 0, 0, -1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), NOW(), NOW()
FROM users WHERE email = 'admin@resumeiq.com';

INSERT IGNORE INTO subscriptions (user_id, plan, status, scans_used_today, total_scans_used, scans_limit, start_date, renewal_date, created_at, updated_at)
SELECT id, 'PRO', 'ACTIVE', 0, 0, -1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH), NOW(), NOW()
FROM users WHERE email = 'demo@resumeiq.com';
