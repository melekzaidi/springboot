# Student Grades URL Fix

## Issue
Students were getting a 404 error when clicking "Mes Notes" from Thymeleaf templates:
```
No static resource student-grades/my-grades
```

## Root Cause
The Thymeleaf templates were incorrectly trying to access `/student-grades/my-grades` but the controller is mapped to `/student-grades`.

## Fix Applied
Updated the following Thymeleaf templates to use the correct URL path:

### Files Updated:
1. **`src/main/resources/templates/calendar.html`**
   - Changed: `th:href="@{/student-grades/my-grades}"`
   - To: `th:href="@{/student-grades}"`

2. **`src/main/resources/templates/home.html`**
   - Changed: `href="/student-grades/my-grades"`
   - To: `href="/student-grades"`
   - Updated both navigation menu and quick actions links

## Current URL Structure
- **Thymeleaf (server-side)**: `/student-grades` → `StudentGradeController` → `student-grades/my-grades.html` template
- **React (client-side)**: `/my-grades` → `MyStudentGrades` component

## Result
✅ Students can now successfully access their grades from both:
- Thymeleaf templates (calendar, home page) via `/student-grades`
- React frontend navigation via `/my-grades`

Both routes show the same content: numerical grades with averages and statistics.