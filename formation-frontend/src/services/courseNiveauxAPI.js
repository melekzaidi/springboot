import api from './api';

const courseNiveauxAPI = {
  getAll: () => api.get('/course-niveaux'),
  getById: (id) => api.get(`/course-niveaux/${id}`),
  create: (courseNiveauData) => api.post('/course-niveaux', courseNiveauData),
  update: (id, courseNiveauData) => api.put(`/course-niveaux/${id}`, courseNiveauData),
  delete: (id) => api.delete(`/course-niveaux/${id}`),
  getByCourse: (courseId) => api.get(`/course-niveaux/by-course/${courseId}`),
  getByNiveau: (niveauId) => api.get(`/course-niveaux/by-niveau/${niveauId}`),
};

export default courseNiveauxAPI;