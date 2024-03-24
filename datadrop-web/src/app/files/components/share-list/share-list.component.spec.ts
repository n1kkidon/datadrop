import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShareListComponent } from './share-list.component';

describe('SharelistComponent', () => {
  let component: ShareListComponent;
  let fixture: ComponentFixture<ShareListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShareListComponent],
    });
    fixture = TestBed.createComponent(ShareListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
