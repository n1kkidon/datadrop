import { FileDto } from './FileDto';
import { UserDto } from './UserDto';

export interface DirectoryDto {
  id: number;
  name: string;
  creationDate: Date;
  lastModifiedDate: Date;
  sharedState: string;
  files: FileDto[];
  subdirectories: DirectoryDto[]; //null 2 layers deep
  parentDirectoryId: number;
  owner: UserDto;
  sharedWithUsers: number[];
}
