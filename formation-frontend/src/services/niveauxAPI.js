import api from './api';

const niveauxAPI = {
  getAll: () => api.get('/niveaux'),
  getById: (id) => api.get(`/niveaux/${id}`),
  create: (niveauData) => api.post('/niveaux', niveauData),
  update: (id, niveauData) => api.put(`/niveaux/${id}`, niveauData),
  delete: (id) => api.delete(`/niveaux/${id}`),
};

export default niveauxAPI;