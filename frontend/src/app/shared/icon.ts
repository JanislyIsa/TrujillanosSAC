import { Component, input } from '@angular/core';
const paths:Record<string,string>={
  box:'M3 7 12 3l9 4v10l-9 4-9-4V7Zm0 0 9 4 9-4M12 11v10M7.5 5l9 4',
  stock:'M4 3h16v18H4zM8 16v-4m4 4V8m4 8v-6',
  movement:'M4 7h15m-4-4 4 4-4 4M20 17H5m4-4-4 4 4 4',
  alert:'m12 3 10 18H2L12 3Zm0 6v5m0 3v.1',
  search:'M21 21l-5-5M18 10a8 8 0 1 1-16 0 8 8 0 0 1 16 0',
  plus:'M12 5v14M5 12h14', close:'m6 6 12 12M6 18 18 6',
  eye:'M2 12s3-7 10-7 10 7 10 7-3 7-10 7S2 12 2 12Zm13 0a3 3 0 1 1-6 0 3 3 0 0 1 6 0',
  edit:'m16 3 5 5-13 13H3v-5L16 3Zm-2 2 5 5',trash:'M3 6h18M9 6V3h6v3M5 6l1 15h12l1-15M10 10v7m4-7v7',
  logout:'M10 4H4v16h6m5-14 6 6-6 6m-8-6h14',chevron:'m9 5 7 7-7 7',
  upload:'M12 16V4m-5 5 5-5 5 5M4 16v5h16v-5',
  check:'m5 12 4 4L20 5',refresh:'M20 7V2m0 5h-5m5 0a9 9 0 1 0 1 9',
  file:'M14 2H5v20h14V7l-5-5Zm0 0v6h5M8 12h8m-8 4h6',
  bell:'M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9M10 21h4',
  menu:'M3 6h18M3 12h18M3 18h18',print:'M6 9V3h12v6M6 18H3V9h18v9h-3M6 14h12v8H6z',
  cart:'M2 3h3l3 13h12l2-9H6M9 20h.1m10 0h.1',image:'M3 3h18v18H3zM3 16l5-5 5 5 3-3 5 5M16 7h.1',
  shield:'m12 2 9 4v6c0 5-9 10-9 10S3 17 3 12V6l9-4Zm-5 10 3 3 7-7',
};
@Component({selector:'app-icon',template:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path [attr.d]="path()" /></svg>',styles:':host{display:inline-flex;width:20px;height:20px;flex:none;align-items:center}svg{width:100%;height:100%}'})
export class Icon { readonly name=input('box'); path(){return paths[this.name()]??paths['box'];} }
