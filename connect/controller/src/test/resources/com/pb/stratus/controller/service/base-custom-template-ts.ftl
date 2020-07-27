import { Component, NgModule, Input} from '@angular/core'
import { CommonModule } from '@angular/common';
import { PbTdCustomTemplateModule } from '../templatecomponents/pb-td-custom-template.module.ts';

@Component({
selector: "td-${templateName}",
templateUrl: '../controller/theme/infotemplates/${templateName}.html',
styleUrls: []
})

export class ${templateName}Component{

@Input() data;

<#list templateElements as key, value>
readonly ${key}Config = ${value};
</#list>

constructor() {}
}

@NgModule({
imports: [CommonModule, PbTdCustomTemplateModule],
declarations: [${templateName}Component ],
exports: [${templateName}Component]
})

export class ${templateName}Module{ };