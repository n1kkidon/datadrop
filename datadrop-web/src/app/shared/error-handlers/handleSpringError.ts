import { ErrorResponse } from '../models/ErrorResponse';
import { of } from 'rxjs';

export function handleSpringError() {
  return (error: ErrorResponse) => {
    console.error(error);
    console.log('ERRORAS');
    return of(error);
  };
}
