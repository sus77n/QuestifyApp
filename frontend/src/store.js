import { configureStore } from "@reduxjs/toolkit";
import { authService } from "./API/service/auth.service";
import { exerciseService } from "./API/service/exercise.service";
import { userService } from "./API/service/user.service";
import { learningUnitService } from "./API/service/learningUnit.service";
import { submissionService } from "./API/service/submission.service";
import { attemptService} from "./API/service/attempt.service";
import { courseService} from "./API/service/course.service";
import {apiErrorMiddleware} from "./apiErrorMiddleware";
import {lessonConfigService} from "./API/service/lessonConfig.service";


export const store = configureStore({
  reducer: {
    [authService.reducerPath]: authService.reducer,
    [exerciseService.reducerPath]: exerciseService.reducer,
    [userService.reducerPath]: userService.reducer,
    [learningUnitService.reducerPath]: learningUnitService.reducer,
    [submissionService.reducerPath]: submissionService.reducer,
    [attemptService.reducerPath]: attemptService.reducer,
    [courseService.reducerPath]: courseService.reducer,
    [lessonConfigService.reducerPath]:lessonConfigService.reducer,

  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      authService.middleware,
      exerciseService.middleware,
      userService.middleware,
      learningUnitService.middleware,
      submissionService.middleware,
        attemptService.middleware,
        courseService.middleware,
        lessonConfigService.middleware,
        apiErrorMiddleware,
    ),
});
