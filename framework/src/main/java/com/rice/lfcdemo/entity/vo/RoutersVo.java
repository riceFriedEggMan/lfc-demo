package com.rice.lfcdemo.entity.vo;

import com.rice.lfcdemo.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutersVo {
    private List<Menu> menus;
}
