import { Component, inject, OnInit, output, signal } from '@angular/core';
import { ZakahCompanyRecordService } from '../../../services/zakah-company-service/zakah-company-service';
import { CurrencyPipe } from '@angular/common';
import { ZakahCompanyRecordSummaryResponse } from '../../../models/response/ZakahCompanyResponse';
import { Router } from '@angular/router';
import {ZakahStatus} from '../../../models/enums/ZakahStatus';
import {AuthStorageService} from '../../../services/storage-service/StorageService';
import {UserType} from '../../../models/enums/UserType';

@Component({
  selector: 'app-after-calc',
  imports:[CurrencyPipe],
  templateUrl: './after-calc.component.html',
  styleUrls: ['./after-calc.component.css']
})
export class AfterCalcComponent implements OnInit {
  type = AuthStorageService.getUserType();
  router = inject(Router);
  constructor() { }
  _zakahService = inject(ZakahCompanyRecordService);

  zakahResult = signal<ZakahCompanyRecordSummaryResponse | null>(null);

  ngOnInit() {
    // Get the latest result from the service
    this.zakahResult.set(this._zakahService.latestResult());
  }

  onViewDashboard() {
      // navigate to dashboard

      this.router.navigate(['/company/dashboard']);
  }

  onStartNewCalculation() {
  this._zakahService.latestResult.set(null);
    if (this.type === UserType.ROLE_COMPANY) {
      this.router.navigate(['/company/wizard']);
    } else {
      this.router.navigate(['/company/company-software/wizard']);
    }
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

