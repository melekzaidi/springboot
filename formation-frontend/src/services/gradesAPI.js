import api from './api';

const gradesAPI = {
  // Get all grades (Admin only)
  getAllGrades: () => api.get('/api/grades'),

  // Get grade by ID
  getGradeById: (id) => api.get(`/api/grades/${id}`),

  // Create new grade
  createGrade: (gradeData) => api.post('/api/grades', gradeData),

  // Update grade
  updateGrade: (id, gradeData) => api.put(`/api/grades/${id}`, gradeData),

  // Delete grade
  deleteGrade: (id) => api.delete(`/api/grades/${id}`),

  // Get grades by professor
  getGradesByProf: (profId) => api.get(`/api/grades/by-prof/${profId}`),

  // Get grades by student
  getGradesByStudent: (studentId) => api.get(`/api/grades/by-student/${studentId}`),

  // Get grades by prof-course-niveau
  getGradesByProfCourseNiveau: (profCourseNiveauId) => 
    api.get(`/api/grades/by-prof-course-niveau/${profCourseNiveauId}`),

  // Get student grades summary
  getStudentGradesSummary: (profCourseNiveauId) => 
    api.get(`/api/grades/summary/by-prof-course-niveau/${profCourseNiveauId}`),

  // Get student moyenne
  getStudentMoyenne: (studentId, profCourseNiveauId) => 
    api.get(`/api/grades/moyenne/${studentId}/${profCourseNiveauId}`),

  // Get grade types
  getGradeTypes: () => api.get('/api/grades/grade-types'),

  // Professor specific endpoints
  getMyGrades: () => api.get('/api/grades/my-grades'),
  
  getMyProfCourseNiveaux: () => api.get('/api/grades/my-prof-course-niveaux'),
  
  createGradeForProf: (gradeData) => api.post('/api/grades/for-prof', gradeData),
  
  getMyStudentGradesSummary: (profCourseNiveauId) => 
    api.get(`/api/grades/summary/my-prof-course-niveau/${profCourseNiveauId}`),

  // Student specific endpoints
  getMyGradesAsStudent: () => api.get('/api/grades/my-grades-as-student'),
  
  getMyGradesSummaryAsStudent: () => api.get('/api/grades/my-summary')
};

export default gradesAPI;