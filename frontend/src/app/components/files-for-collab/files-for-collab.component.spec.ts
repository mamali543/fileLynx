import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilesForCollabComponent } from './files-for-collab.component';

describe('FilesForCollabComponent', () => {
  let component: FilesForCollabComponent;
  let fixture: ComponentFixture<FilesForCollabComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FilesForCollabComponent]
    });
    fixture = TestBed.createComponent(FilesForCollabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
