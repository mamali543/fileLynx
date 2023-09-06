import { TestBed } from '@angular/core/testing';

import { CollabServiceService } from './collab-service.service';

describe('CollabServiceService', () => {
  let service: CollabServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CollabServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
