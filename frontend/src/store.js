import { configureStore } from '@reduxjs/toolkit';
import { authService } from './API/service/auth.service';
import { courseService } from './API/service/course.service';
import { lessonService } from './API/service/lesson.service';
import { exerciseService } from './API/service/exercise.service';
import { chapterService } from './API/service/chapter.service';

export const store = configureStore({
    reducer: {
        [authService.reducerPath]: authService.reducer,
        [courseService.reducerPath]: courseService.reducer,
        [lessonService.reducerPath]: lessonService.reducer,
        [exerciseService.reducerPath]: exerciseService.reducer,
        [chapterService.reducerPath]: chapterService.reducer,
    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware().concat(
            authService.middleware,
            courseService.middleware,
            lessonService.middleware,
            exerciseService.middleware,
            chapterService.middleware
        ),
});