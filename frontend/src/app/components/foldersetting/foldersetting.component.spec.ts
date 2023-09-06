import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FoldersettingComponent } from './foldersetting.component';

describe('FoldersettingComponent', () => {
  let component: FoldersettingComponent;
  let fixture: ComponentFixture<FoldersettingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FoldersettingComponent]
    });
    fixture = TestBed.createComponent(FoldersettingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
