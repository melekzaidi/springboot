import api from './api';

const studentsAPI = {
  getAll: (keyword = '') => {
    const params = keyword ? { keyword } : {};
    return api.get('/students', { params });
  },
  getById: (id) => api.get(`/students/${id}`),
  create: (studentData) => api.post('/students', studentData),
  update: (id, studentData) => api.put(`/students/${id}`, studentData),
  delete: (id) => api.delete(`/students/${id}`),
  getCount: () => api.get('/students/count'),
};

export default studentsAPI;