import { configureStore } from '@reduxjs/toolkit';
import authSlice from './slices/authSlice';
import studentsSlice from './slices/studentsSlice';
import professorsSlice from './slices/professorsSlice';
import coursesSlice from './slices/coursesSlice';
import sessionsSlice from './slices/sessionsSlice';
import niveauxSlice from './slices/niveauxSlice';
import gradesSlice from './slices/gradesSlice';
import professorNotesSlice from './slices/professorNotesSlice';

export const store = configureStore({
  reducer: {
    auth: authSlice,
    students: studentsSlice,
    professors: professorsSlice,
    courses: coursesSlice,
    sessions: sessionsSlice,
    niveaux: niveauxSlice,
    grades: gradesSlice,
    professorNotes: professorNotesSlice,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST'],
      },
    }),
});