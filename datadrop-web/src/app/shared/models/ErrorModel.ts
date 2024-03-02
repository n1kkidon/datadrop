export interface ErrorModel {
  status: number;
  errors: error[];
  message: string;
}

export interface error {
  propertyName: string;
  errorMessage: string;
}
