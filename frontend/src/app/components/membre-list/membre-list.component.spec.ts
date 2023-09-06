import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembreListComponent } from './membre-list.component';

describe('MembreListComponent', () => {
  let component: MembreListComponent;
  let fixture: ComponentFixture<MembreListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MembreListComponent]
    });
    fixture = TestBed.createComponent(MembreListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
