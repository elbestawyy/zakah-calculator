import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ZakahGuide } from './zakah-guide';

describe('ZakahGuide', () => {
  let component: ZakahGuide;
  let fixture: ComponentFixture<ZakahGuide>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ZakahGuide]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ZakahGuide);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
