import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembersettingComponent } from './membersetting.component';

describe('MembersettingComponent', () => {
  let component: MembersettingComponent;
  let fixture: ComponentFixture<MembersettingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MembersettingComponent]
    });
    fixture = TestBed.createComponent(MembersettingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
