import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditFormDialogComponent } from './editform-dialog.component';

describe('DirectoryDialogComponent', () => {
  let component: EditFormDialogComponent;
  let fixture: ComponentFixture<EditFormDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditFormDialogComponent],
    });
    fixture = TestBed.createComponent(EditFormDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
