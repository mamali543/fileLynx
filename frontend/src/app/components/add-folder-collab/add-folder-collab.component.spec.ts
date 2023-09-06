import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddFolderCollabComponent } from './add-folder-collab.component';

describe('AddFolderCollabComponent', () => {
  let component: AddFolderCollabComponent;
  let fixture: ComponentFixture<AddFolderCollabComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddFolderCollabComponent]
    });
    fixture = TestBed.createComponent(AddFolderCollabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
