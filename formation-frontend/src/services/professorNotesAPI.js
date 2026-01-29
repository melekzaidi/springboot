import api from './api';

const professorNotesAPI = {
  // Professor endpoints
  createNote: (noteData) => api.post('/api/professor-notes', noteData),
  
  updateNote: (noteId, noteData) => api.put(`/api/professor-notes/${noteId}`, noteData),
  
  deleteNote: (noteId) => api.delete(`/api/professor-notes/${noteId}`),
  
  getNoteById: (noteId) => api.get(`/api/professor-notes/${noteId}`),
  
  getMyNotes: () => api.get('/api/professor-notes/my-notes'),
  
  getNotesByStudent: (studentId) => api.get(`/api/professor-notes/student/${studentId}`),
  
  getNotesByCourse: (profCourseNiveauId) => api.get(`/api/professor-notes/course/${profCourseNiveauId}`),
  
  // Student endpoints
  getMyStudentNotes: () => api.get('/api/professor-notes/my-student-notes'),
  
  getMyStudentNotesInCourse: (profCourseNiveauId) => api.get(`/api/professor-notes/my-student-notes/course/${profCourseNiveauId}`),
  
  // Bulk operations
  createBulkNotes: (notesData) => api.post('/api/professor-notes/bulk', notesData),
  
  updateBulkNotes: (notesData) => api.put('/api/professor-notes/bulk', notesData),
  
  deleteBulkNotes: (noteIds) => api.delete('/api/professor-notes/bulk', { data: noteIds }),
};

export default professorNotesAPI;