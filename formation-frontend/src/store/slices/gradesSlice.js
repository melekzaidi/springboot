import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import gradesAPI from '../../services/gradesAPI';

// Async thunks
export const fetchGrades = createAsyncThunk(
  'grades/fetchGrades',
  async (_, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.getAllGrades();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des notes');
    }
  }
);

export const fetchGradesByProfCourseNiveau = createAsyncThunk(
  'grades/fetchGradesByProfCourseNiveau',
  async (profCourseNiveauId, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.getGradesByProfCourseNiveau(profCourseNiveauId);
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des notes');
    }
  }
);

export const fetchMyGrades = createAsyncThunk(
  'grades/fetchMyGrades',
  async (_, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.getMyGrades();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération de vos notes');
    }
  }
);

export const fetchMyProfCourseNiveaux = createAsyncThunk(
  'grades/fetchMyProfCourseNiveaux',
  async (_, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.getMyProfCourseNiveaux();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération de vos affectations');
    }
  }
);

export const fetchStudentGradesSummary = createAsyncThunk(
  'grades/fetchStudentGradesSummary',
  async (profCourseNiveauId, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.getStudentGradesSummary(profCourseNiveauId);
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération du résumé des notes');
    }
  }
);

export const fetchMyStudentGradesSummary = createAsyncThunk(
  'grades/fetchMyStudentGradesSummary',
  async (profCourseNiveauId, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.getMyStudentGradesSummary(profCourseNiveauId);
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération du résumé des notes');
    }
  }
);

export const createGradeForProf = createAsyncThunk(
  'grades/createGradeForProf',
  async (gradeData, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.createGradeForProf(gradeData);
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création de la note');
    }
  }
);

export const createGrade = createAsyncThunk(
  'grades/createGrade',
  async (gradeData, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.createGrade(gradeData);
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création de la note');
    }
  }
);

export const updateGrade = createAsyncThunk(
  'grades/updateGrade',
  async ({ id, gradeData }, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.updateGrade(id, gradeData);
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la modification de la note');
    }
  }
);

export const deleteGrade = createAsyncThunk(
  'grades/deleteGrade',
  async (id, { rejectWithValue }) => {
    try {
      await gradesAPI.deleteGrade(id);
      return id;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression de la note');
    }
  }
);

export const fetchGradeTypes = createAsyncThunk(
  'grades/fetchGradeTypes',
  async (_, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.getGradeTypes();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des types de notes');
    }
  }
);

export const fetchMyGradesAsStudent = createAsyncThunk(
  'grades/fetchMyGradesAsStudent',
  async (_, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.getMyGradesAsStudent();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération de vos notes');
    }
  }
);

export const fetchMyGradesSummaryAsStudent = createAsyncThunk(
  'grades/fetchMyGradesSummaryAsStudent',
  async (_, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.getMyGradesSummaryAsStudent();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération de votre résumé de notes');
    }
  }
);

export const fetchMyStudentGrades = createAsyncThunk(
  'grades/fetchMyStudentGrades',
  async (_, { rejectWithValue }) => {
    try {
      const response = await gradesAPI.getMyGradesAsStudent();
      return response.data.data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération de vos notes');
    }
  }
);

const gradesSlice = createSlice({
  name: 'grades',
  initialState: {
    grades: [],
    studentGradesSummary: [],
    myProfCourseNiveaux: [],
    gradeTypes: [],
    currentGrade: null,
    loading: false,
    error: null,
    success: null
  },
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearSuccess: (state) => {
      state.success = null;
    },
    setCurrentGrade: (state, action) => {
      state.currentGrade = action.payload;
    },
    clearCurrentGrade: (state) => {
      state.currentGrade = null;
    }
  },
  extraReducers: (builder) => {
    builder
      // Fetch grades
      .addCase(fetchGrades.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchGrades.fulfilled, (state, action) => {
        state.loading = false;
        state.grades = action.payload;
      })
      .addCase(fetchGrades.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch grades by prof-course-niveau
      .addCase(fetchGradesByProfCourseNiveau.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchGradesByProfCourseNiveau.fulfilled, (state, action) => {
        state.loading = false;
        state.grades = action.payload;
      })
      .addCase(fetchGradesByProfCourseNiveau.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch my grades
      .addCase(fetchMyGrades.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyGrades.fulfilled, (state, action) => {
        state.loading = false;
        state.grades = action.payload;
      })
      .addCase(fetchMyGrades.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch my prof course niveaux
      .addCase(fetchMyProfCourseNiveaux.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyProfCourseNiveaux.fulfilled, (state, action) => {
        state.loading = false;
        state.myProfCourseNiveaux = action.payload;
      })
      .addCase(fetchMyProfCourseNiveaux.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch my student grades summary
      .addCase(fetchMyStudentGradesSummary.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyStudentGradesSummary.fulfilled, (state, action) => {
        state.loading = false;
        state.studentGradesSummary = action.payload;
      })
      .addCase(fetchMyStudentGradesSummary.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Create grade for prof
      .addCase(createGradeForProf.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createGradeForProf.fulfilled, (state, action) => {
        state.loading = false;
        state.grades.push(action.payload);
        state.success = 'Note créée avec succès';
      })
      .addCase(createGradeForProf.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch student grades summary
      .addCase(fetchStudentGradesSummary.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchStudentGradesSummary.fulfilled, (state, action) => {
        state.loading = false;
        state.studentGradesSummary = action.payload;
      })
      .addCase(fetchStudentGradesSummary.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Create grade
      .addCase(createGrade.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createGrade.fulfilled, (state, action) => {
        state.loading = false;
        state.grades.push(action.payload);
        state.success = 'Note créée avec succès';
      })
      .addCase(createGrade.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Update grade
      .addCase(updateGrade.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateGrade.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.grades.findIndex(grade => grade.id === action.payload.id);
        if (index !== -1) {
          state.grades[index] = action.payload;
        }
        state.success = 'Note modifiée avec succès';
      })
      .addCase(updateGrade.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Delete grade
      .addCase(deleteGrade.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteGrade.fulfilled, (state, action) => {
        state.loading = false;
        state.grades = state.grades.filter(grade => grade.id !== action.payload);
        state.success = 'Note supprimée avec succès';
      })
      .addCase(deleteGrade.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch grade types
      .addCase(fetchGradeTypes.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchGradeTypes.fulfilled, (state, action) => {
        state.loading = false;
        state.gradeTypes = action.payload;
      })
      .addCase(fetchGradeTypes.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch my grades as student
      .addCase(fetchMyGradesAsStudent.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyGradesAsStudent.fulfilled, (state, action) => {
        state.loading = false;
        state.grades = action.payload;
      })
      .addCase(fetchMyGradesAsStudent.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch my grades summary as student
      .addCase(fetchMyGradesSummaryAsStudent.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyGradesSummaryAsStudent.fulfilled, (state, action) => {
        state.loading = false;
        state.studentGradesSummary = action.payload;
      })
      .addCase(fetchMyGradesSummaryAsStudent.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch my student grades
      .addCase(fetchMyStudentGrades.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyStudentGrades.fulfilled, (state, action) => {
        state.loading = false;
        state.grades = action.payload;
      })
      .addCase(fetchMyStudentGrades.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  }
});

export const { clearError, clearSuccess, setCurrentGrade, clearCurrentGrade } = gradesSlice.actions;
export default gradesSlice.reducer;