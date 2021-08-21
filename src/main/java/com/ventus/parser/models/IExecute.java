package com.ventus.parser.models;


import com.ventus.parser.modules.AnalyticsModule;

import java.util.List;

public interface IExecute {
    void execute(List<String> list, AnalyticsModule analyticsModule);
}
