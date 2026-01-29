# Thymeleaf Fragment Signature Fix

## Issue
Students were getting a 500 Internal Server Error when accessing their grades:
```
Cannot resolve fragment. Signature "layout (content)" declares 1 parameters, but fragment selection specifies 2 parameters.
```

## Root Cause
The Thymeleaf templates were using an incorrect fragment signature:
- **Incorrect**: `th:replace="~{layout :: layout(~{::title}, ~{::main})}"`
- **Correct**: `th:replace="~{layout :: layout(~{::main})}"`

The layout fragment in `layout.html` is defined as:
```html
<html th:fragment="layout (content)">
```
This means it only accepts **1 parameter** (content), not 2 parameters (title and main).

## Fix Applied
Updated the following templates to use the correct fragment signature:

### Files Fixed:
1. **`src/main/resources/templates/student-grades/my-grades.html`**
   - Changed: `th:replace="~{layout :: layout(~{::title}, ~{::main})}"`
   - To: `th:replace="~{layout :: layout(~{::main})}"`

2. **`src/main/resources/templates/sessions/list-with-layout.html`**
   - Changed: `th:replace="~{layout :: layout(~{::title}, ~{::main})}"`
   - To: `th:replace="~{layout :: layout(~{::main})}"`

## Verification
Checked all other templates and confirmed they are using the correct signature:
- ✅ `grades/my-assignments.html`
- ✅ `grades/list.html`
- ✅ `grades/form.html`
- ✅ `grades/by-prof-course-niveau.html`

## Result
✅ Students can now successfully access their grades via `/student-grades`
✅ No more template parsing errors
✅ The layout fragment works correctly with the single content parameter

## Technical Details
The layout template (`layout.html`) uses the page title from the controller's model attribute `pageTitle`, not from the template's title tag. This is why only the main content needs to be passed to the fragment.

Controller sets: `model.addAttribute("pageTitle", "Mes Notes");`
Layout uses: `<title th:text="${pageTitle} + ' - Centre de Formation'">Centre de Formation</title>`