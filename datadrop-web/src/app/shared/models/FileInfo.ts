import { UserDto } from './UserDto';

export interface FileInfo {
  id: number;
  name: string;
  creationDate: Date;
  lastModifiedDate: Date;
  sharedState: string;
  sharedWithUsers: number[];
  mimeType: string;
  parentDirectoryId: number;
  owner: UserDto;
}
