export interface ShareStateUpdateRequest {
  itemId: number;
  state: string;
  shareWithUserIds: number[];
  stopSharingWithUserIds: number[];
}
