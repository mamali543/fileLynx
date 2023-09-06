import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilesettingsComponent } from './filesettings.component';

describe('FilesettingsComponent', () => {
  let component: FilesettingsComponent;
  let fixture: ComponentFixture<FilesettingsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FilesettingsComponent]
    });
    fixture = TestBed.createComponent(FilesettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
