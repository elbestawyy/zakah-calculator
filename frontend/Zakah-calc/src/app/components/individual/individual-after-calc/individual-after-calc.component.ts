import {Component, inject, OnInit, signal} from '@angular/core';
import {Router} from '@angular/router';
import {ZakahIndividualRecordService} from '../../../services/zakah-individual-service/zakah-individual-service';
import {ZakahIndividualRecordResponse} from '../../../models/response/ZakahIndividualResponse';
import {CurrencyPipe} from '@angular/common';
import {ZakahStatus} from '../../../models/enums/ZakahStatus';

@Component({
  selector: 'app-individual-after-calc',
  templateUrl: './individual-after-calc.component.html',
  styleUrls: ['./individual-after-calc.component.css'],
  imports: [CurrencyPipe],
})
export class IndividualAfterCalcComponent implements OnInit {

router = inject(Router);
  constructor() { }
  _zakahService = inject(ZakahIndividualRecordService);

  zakahResult = signal<ZakahIndividualRecordResponse | null>(null);

  ngOnInit() {
    // Get the latest result from the service
    this.zakahResult.set(this._zakahService.latestResult());
  }

  onViewDashboard() {
      // navigate to dashboard

      this.router.navigate(['/individual/dashboard']);
  }

  onStartNewCalculation() {
  this._zakahService.latestResult.set(null);
  this.router.navigate(['/individual/wizard']); // go back to wizard
}

  zakahStatusMessageMap: Record<ZakahStatus, string> = {
    [ZakahStatus.BELOW_NISAB]:
      'المال أقل من النصاب، ولا تجب عليه الزكاة',

    [ZakahStatus.ELIGABLE_FOR_ZAKAH]:
      'المال بلغ النصاب، في انتظار اكتمال الحول',

    [ZakahStatus.HAWL_NOT_COMPLETED]:
      'الحول لم يكتمل بعد، الزكاة غير مستحقة حاليًا',

    [ZakahStatus.ZAKAH_DUE]:
      'الزكاة مستحقة ويجب إخراجها الآن',

    [ZakahStatus.LAST_RECORD_DUE_AND_NEW_HAWL_BEGIN]:
      'تم إخراج زكاة الحول السابق وبدأ حول جديد',
  };

}
