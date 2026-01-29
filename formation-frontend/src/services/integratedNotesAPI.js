import api from './api';

const integratedNotesAPI = {
  // Student endpoints
  getStudentIntegratedData: () => api.get('/api/integrated-notes/student-data'),
  getStudentStats: () => api.get('/api/integrated-notes/student-stats'),
  
  // Professor endpoints
  getProfessorIntegratedData: () => api.get('/api/integrated-notes/professor-data'),
  getProfessorStats: () => api.get('/api/integrated-notes/professor-stats'),
  
  // Grade management (for professors)
  createGrade: (gradeData) => api.post('/api/integrated-notes/grades', gradeData),
  updateGrade: (id, gradeData) => api.put(`/api/integrated-notes/grades/${id}`, gradeData),
  deleteGrade: (id) => api.delete(`/api/integrated-notes/grades/${id}`),
  
  // Remark management (for professors)
  createRemark: (remarkData) => api.post('/api/integrated-notes/remarks', remarkData),
  updateRemark: (id, remarkData) => api.put(`/api/integrated-notes/remarks/${id}`, remarkData),
  deleteRemark: (id) => api.delete(`/api/integrated-notes/remarks/${id}`),
  
  // Utility endpoints
  getGradeTypes: () => api.get('/api/integrated-notes/grade-types'),
  getAllStudents: () => api.get('/api/integrated-notes/students'),
};

export default integratedNotesAPI;