import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntreprisesComponent } from './entreprises.component';

describe('EntreprisesComponent', () => {
  let component: EntreprisesComponent;
  let fixture: ComponentFixture<EntreprisesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EntreprisesComponent]
    });
    fixture = TestBed.createComponent(EntreprisesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
