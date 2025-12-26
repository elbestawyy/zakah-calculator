// src/app/shared/tooltip/tooltip.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-tooltip',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="relative inline-block">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-slate-400 cursor-help" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <div class="absolute bottom-full left-1/2 transform -translate-x-1/2 mb-2 px-3 py-2 bg-slate-800 text-white text-sm rounded-lg w-64 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-300 z-50">
        {{ text }}
        <div class="absolute top-full left-1/2 transform -translate-x-1/2 -mt-1">
          <div class="border-4 border-transparent border-t-slate-800"></div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: inline-block;
    }
    .group:hover .group-hover\\:visible {
      visibility: visible;
    }
    .group:hover .group-hover\\:opacity-100 {
      opacity: 1;
    }
  `]
})
export class TooltipComponent {
  @Input() text: string = '';
}
