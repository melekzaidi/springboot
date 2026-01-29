import api from './api';

const gradesAPI = {
  // Get all grades
  getAllGrades: () => api.get('/grades'),

  // Get grade by ID
  getGradeById: (id) => api.get(`/grades/${id}`),

  // Create new grade
  createGrade: (gradeData) => api.post('/grades', gradeData),

  // Update grade
  updateGrade: (id, gradeData) => api.put(`/grades/${id}`, gradeData),

  // Delete grade
  deleteGrade: (id) => api.delete(`/grades/${id}`),

  // Get grades by professor
  getGradesByProf: (profId) => api.get(`/grades/by-prof/${profId}`),

  // Get grades by student
  getGradesByStudent: (studentId) => api.get(`/grades/by-student/${studentId}`),

  // Get grades by prof-course-niveau
  getGradesByProfCourseNiveau: (profCourseNiveauId) => 
    api.get(`/grades/by-prof-course-niveau/${profCourseNiveauId}`),

  // Get student grades summary
  getStudentGradesSummary: (profCourseNiveauId) => 
    api.get(`/grades/summary/by-prof-course-niveau/${profCourseNiveauId}`),

  // Get student moyenne
  getStudentMoyenne: (studentId, profCourseNiveauId) => 
    api.get(`/grades/moyenne/${studentId}/${profCourseNiveauId}`),

  // Get grade types
  getGradeTypes: () => api.get('/grades/grade-types'),

  // Professor specific endpoints
  getMyGrades: () => api.get('/grades/my-grades'),
  
  getMyProfCourseNiveaux: () => api.get('/grades/my-prof-course-niveaux'),
  
  createGradeForProf: (gradeData) => api.post('/grades/for-prof', gradeData),
  
  getMyStudentGradesSummary: (profCourseNiveauId) => 
    api.get(`/grades/summary/my-prof-course-niveau/${profCourseNiveauId}`),

  // Student specific endpoints
  getMyGradesAsStudent: () => api.get('/grades/my-grades-as-student'),
  
  getMyGradesSummaryAsStudent: () => api.get('/grades/my-summary')
};

export default gradesAPI;