<div>
<#list templateElements as element>
    <#switch element.elementType>
        <#case "Column">
            <pb-info-column [columnConfig]="${element.name}Config" [feature]="data.feature" class="multilabelrow col-xs-12 default-topbtm-padding">
            </pb-info-column>
            <#break>
        <#case "PieChart">
            <pb-info-pie-chart [chartConfig]="${element.id}Config" [feature]="data.feature" class="multilabelrow col-xs-12 default-topbtm-padding">
            </pb-info-pie-chart>
            <#break>
        <#case "BarChart">
            <pb-info-bar-chart [chartConfig]="${element.id}Config" [feature]="data.feature" class="multilabelrow col-xs-12 default-topbtm-padding">
            </pb-info-bar-chart>
            <#break>
        <#case "GoogleStreetView">
            <pb-info-google-street-view [viewConfig]="${element.id}Config" [feature]="data.feature" class="multilabelrow col-xs-12 default-topbtm-padding">
            </pb-info-google-street-view>
        <#default>
    </#switch>
</#list>
</div>