import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntreprisesListComponent } from './entreprises-list.component';

describe('EntreprisesListComponent', () => {
  let component: EntreprisesListComponent;
  let fixture: ComponentFixture<EntreprisesListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EntreprisesListComponent]
    });
    fixture = TestBed.createComponent(EntreprisesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
