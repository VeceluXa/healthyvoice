package com.danilovfa.common.base.component.stateful

import com.danilovfa.common.base.component.BaseComponent

interface StatefulComponent<Intent : Any, State: Any> : BaseComponent, Stateful<Intent, State>
