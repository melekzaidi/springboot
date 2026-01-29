import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Box,
  Typography,
  Paper,
  Grid,
  Card,
  CardContent,
  Button,
  Chip,
  IconButton,
} from '@mui/material';
import {
  ChevronLeft as ChevronLeftIcon,
  ChevronRight as ChevronRightIcon,
  Today as TodayIcon,
} from '@mui/icons-material';
import { fetchSessions } from '../../store/slices/sessionsSlice';
import dayjs from 'dayjs';
import 'dayjs/locale/fr';

dayjs.locale('fr');

const Calendar = () => {
  const dispatch = useDispatch();
  const { sessions, loading } = useSelector((state) => state.sessions);
  const { user } = useSelector((state) => state.auth);
  const [currentDate, setCurrentDate] = useState(dayjs());

  useEffect(() => {
    dispatch(fetchSessions());
  }, [dispatch]);

  const startOfWeek = currentDate.startOf('week').add(1, 'day'); // Start on Monday
  const endOfWeek = startOfWeek.add(6, 'day');

  const weekDays = [];
  for (let i = 0; i < 7; i++) {
    weekDays.push(startOfWeek.add(i, 'day'));
  }

  const getSessionsForDay = (day) => {
    return sessions.filter(session => {
      const sessionDate = dayjs(session.startDateTime);
      return sessionDate.isSame(day, 'day');
    });
  };

  const getSessionStatus = (session) => {
    const now = dayjs();
    const startTime = dayjs(session.startDateTime);
    const endTime = dayjs(session.endDateTime);

    if (now.isBefore(startTime)) {
      return { label: 'À venir', color: 'info' };
    } else if (now.isAfter(startTime) && now.isBefore(endTime)) {
      return { label: 'En cours', color: 'success' };
    } else {
      return { label: 'Terminée', color: 'default' };
    }
  };

  const formatTime = (dateTimeString) => {
    return dayjs(dateTimeString).format('HH:mm');
  };

  const goToPreviousWeek = () => {
    setCurrentDate(currentDate.subtract(1, 'week'));
  };

  const goToNextWeek = () => {
    setCurrentDate(currentDate.add(1, 'week'));
  };

  const goToToday = () => {
    setCurrentDate(dayjs());
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">
          Calendrier des séances
        </Typography>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <IconButton onClick={goToPreviousWeek}>
            <ChevronLeftIcon />
          </IconButton>
          <Button
            variant="outlined"
            startIcon={<TodayIcon />}
            onClick={goToToday}
          >
            Aujourd'hui
          </Button>
          <IconButton onClick={goToNextWeek}>
            <ChevronRightIcon />
          </IconButton>
        </Box>
      </Box>

      <Paper sx={{ p: 2, mb: 3 }}>
        <Typography variant="h6" align="center">
          Semaine du {startOfWeek.format('DD MMMM')} au {endOfWeek.format('DD MMMM YYYY')}
        </Typography>
      </Paper>

      <Grid container spacing={2}>
        {weekDays.map((day, index) => {
          const daySessions = getSessionsForDay(day);
          const isToday = day.isSame(dayjs(), 'day');
          
          return (
            <Grid item xs={12} md={12/7} key={index}>
              <Paper 
                sx={{ 
                  minHeight: 300,
                  border: isToday ? 2 : 0,
                  borderColor: 'primary.main',
                }}
              >
                <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
                  <Typography 
                    variant="h6" 
                    align="center"
                    color={isToday ? 'primary' : 'inherit'}
                    fontWeight={isToday ? 'bold' : 'normal'}
                  >
                    {day.format('dddd')}
                  </Typography>
                  <Typography 
                    variant="body2" 
                    align="center"
                    color={isToday ? 'primary' : 'textSecondary'}
                  >
                    {day.format('DD/MM')}
                  </Typography>
                </Box>
                
                <Box sx={{ p: 1 }}>
                  {daySessions.map((session) => {
                    const status = getSessionStatus(session);
                    return (
                      <Card 
                        key={session.id} 
                        sx={{ 
                          mb: 1, 
                          cursor: 'pointer',
                          '&:hover': {
                            boxShadow: 2,
                          }
                        }}
                        onClick={() => window.location.href = `/sessions/view/${session.id}`}
                      >
                        <CardContent sx={{ p: 1, '&:last-child': { pb: 1 } }}>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
                            <Typography variant="caption" color="textSecondary">
                              {formatTime(session.startDateTime)} - {formatTime(session.endDateTime)}
                            </Typography>
                            <Chip 
                              label={status.label} 
                              color={status.color} 
                              size="small"
                              sx={{ fontSize: '0.6rem', height: 16 }}
                            />
                          </Box>
                          <Typography variant="body2" fontWeight="medium" noWrap>
                            {session.courseName}
                          </Typography>
                          <Typography variant="caption" color="textSecondary" noWrap>
                            {session.profNom}
                          </Typography>
                          <Typography variant="caption" color="textSecondary" display="block" noWrap>
                            {session.niveauName}
                          </Typography>
                        </CardContent>
                      </Card>
                    );
                  })}
                  
                  {daySessions.length === 0 && (
                    <Typography 
                      variant="body2" 
                      color="textSecondary" 
                      align="center"
                      sx={{ mt: 2 }}
                    >
                      Aucune séance
                    </Typography>
                  )}
                </Box>
              </Paper>
            </Grid>
          );
        })}
      </Grid>

      {user?.role === 'STUDENT' && (
        <Paper sx={{ p: 2, mt: 3 }}>
          <Typography variant="body2" color="textSecondary">
            <strong>Légende :</strong> Cliquez sur une séance pour voir les détails et marquer votre présence.
          </Typography>
        </Paper>
      )}

      {(user?.role === 'PROF' || user?.role === 'ADMIN') && (
        <Paper sx={{ p: 2, mt: 3 }}>
          <Typography variant="body2" color="textSecondary">
            <strong>Légende :</strong> Cliquez sur une séance pour gérer les présences et voir les détails.
          </Typography>
        </Paper>
      )}
    </Box>
  );
};

export default Calendar;