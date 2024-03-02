import { FileInfo } from './FileInfo';
import { UserDto } from './UserDto';

export interface DirectoryInfo {
  id: number;
  name: string;
  creationDate: Date;
  lastModifiedDate: Date;
  sharedState: string;
  files: FileInfo[];
  subdirectories: DirectoryInfo[]; //null 2 layers deep
  parentDirectoryId: number;
  owner: UserDto;
  sharedWithUsers: number[];
}
