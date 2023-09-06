import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserSearchPromptComponent } from './user-search-prompt.component';

describe('UserSearchPromptComponent', () => {
  let component: UserSearchPromptComponent;
  let fixture: ComponentFixture<UserSearchPromptComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserSearchPromptComponent]
    });
    fixture = TestBed.createComponent(UserSearchPromptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
