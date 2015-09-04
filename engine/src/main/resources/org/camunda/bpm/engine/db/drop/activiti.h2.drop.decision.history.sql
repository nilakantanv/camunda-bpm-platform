drop index ACT_IDX_HI_DEC_INST_ID;
drop index ACT_IDX_HI_DEC_INST_KEY;
drop index ACT_IDX_HI_DEC_INST_PI;
drop index ACT_IDX_HI_DEC_INST_ACT;
drop index ACT_IDX_HI_DEC_INST_ACT_INST;
drop index ACT_IDX_HI_DEC_INST_TIME;

create index ACT_IDX_HI_DEC_IN_INST;
create index ACT_IDX_HI_DEC_IN_CLAUSE;

create index ACT_IDX_HI_DEC_OUT_INST;
create index ACT_IDX_HI_DEC_OUT_RULE;

drop table ACT_HI_DECINST if exists;

drop table ACT_HI_DEC_IN if exists;

drop table ACT_HI_DEC_OUT if exists;