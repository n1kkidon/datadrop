import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilepageComponent } from './filepage.component';

describe('FilepageComponent', () => {
  let component: FilepageComponent;
  let fixture: ComponentFixture<FilepageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FilepageComponent],
    });
    fixture = TestBed.createComponent(FilepageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
