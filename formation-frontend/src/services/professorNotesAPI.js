import api from './api';

const professorNotesAPI = {
  // Professor endpoints
  createNote: (noteData) => api.post('/professor-notes', noteData),
  
  updateNote: (noteId, noteData) => api.put(`/professor-notes/${noteId}`, noteData),
  
  deleteNote: (noteId) => api.delete(`/professor-notes/${noteId}`),
  
  getNoteById: (noteId) => api.get(`/professor-notes/${noteId}`),
  
  getMyNotes: () => api.get('/professor-notes/my-notes'),
  
  getNotesByStudent: (studentId) => api.get(`/professor-notes/student/${studentId}`),
  
  getNotesByCourse: (profCourseNiveauId) => api.get(`/professor-notes/course/${profCourseNiveauId}`),
  
  // Student endpoints
  getMyStudentNotes: () => api.get('/professor-notes/my-student-notes'),
  
  getMyStudentNotesInCourse: (profCourseNiveauId) => api.get(`/professor-notes/my-student-notes/course/${profCourseNiveauId}`),
  
  // Bulk operations
  createBulkNotes: (notesData) => api.post('/professor-notes/bulk', notesData),
  
  updateBulkNotes: (notesData) => api.put('/professor-notes/bulk', notesData),
  
  deleteBulkNotes: (noteIds) => api.delete('/professor-notes/bulk', { data: noteIds }),
};

export default professorNotesAPI;