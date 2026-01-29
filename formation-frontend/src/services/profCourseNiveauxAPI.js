import api from './api';

const profCourseNiveauxAPI = {
  // Get all prof-course-niveaux
  getAll: () => api.get('/prof-course-niveaux'),

  // Get prof-course-niveau by ID
  getById: (id) => api.get(`/prof-course-niveaux/${id}`),

  // Create new prof-course-niveau
  create: (data) => api.post('/prof-course-niveaux', data),

  // Update prof-course-niveau
  update: (id, data) => api.put(`/prof-course-niveaux/${id}`, data),

  // Delete prof-course-niveau
  delete: (id) => api.delete(`/prof-course-niveaux/${id}`),

  // Get by professor
  getByProf: (profId) => api.get(`/prof-course-niveaux/by-prof/${profId}`),

  // Get by course-niveau
  getByCourseNiveau: (courseNiveauId) => api.get(`/prof-course-niveaux/by-course-niveau/${courseNiveauId}`)
};

export default profCourseNiveauxAPI;