export interface SoftwareCompanyModel {
// البنود الداخلة الوعاء الزكوي
  generatingFixedAssets: number;       // الموجودات الثابتة الدائرة للدخل
  investmentsEquity: number;           // الاستثمارات في الأسهم
  affiliatesSubsidiaries: number;      // الاستثمارات في الشركات التابعة/الزميلة
  investmentsSukukIslamic: number;     // صكوك الاستثمار الإسلامية
  receivablesTrade: number;            // المدينون
  receivableNotes: number;             // أوراق القبض
  incomeAccrued: number;               // الإيرادات المستحقة
  expensesContractPrepaid: number;     // المبالغ المدفوعة مقدماً عن العقود
  handOnCash: number;                  // النقدية في الصندوق
  accountsCurrentDepositsBank: number; // الودائع والحسابات الجارية لدى البنوك
  depositsStatutory: number;           // الوديعة القانونية
  securitiesTrading: number;           // الاستثمارات في الأسهم بغرض المتاجرة
  partiesThirdWithDeposits: number;    // التأمينات لدى الغير

  // البنود المخصومة من الوعاء الزكوي
  assetsFixedProvisionDepreciation: number;    // مخصص استهلاك الموجودات الثابتة
  provisionOverhaulMaintenance: number;       // مخصص الصيانة أو العمرة
  assetsProvisionInsurance: number;           // مخصص التأمين على الموجودات
  investmentsProvisionImpairment: number;     // مخصص هبوط قيمة الاستثمارات
  debtsDoubtfulAllowance: number;             // مخصص الديون المشكوك في تحصيلها
  discountsCashProvision: number;             // مخصص الخصم النقدي
  payableAccounts: number;                    // الدائنون
  payableNotes: number;                       // أوراق الدفع
  loansTermShort: number;                     // القروض القصيرة الأجل
  debtTermLongPortionCurrent: number;        // القسط الواجب السداد من القروض الطويلة
  expensesAccrued: number;                    // المصروفات المستحقة
  balanceSheetDate: string;
  goldPrice: number;
}
