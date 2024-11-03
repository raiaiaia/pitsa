package com.ufcg.psoft.pitsa.listener;

import com.ufcg.psoft.pitsa.event.EntregadorIndisponivelEvent;

public interface EntregadorIndisponivelListener {
    void notificaEntregadorIndisponivel(EntregadorIndisponivelEvent event);
}
