#
# Copyright 2019 GridGain Systems, Inc. and Contributors.
#
# Licensed under the GridGain Community Edition License (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
ACLOCAL_AMFLAGS =-I m4

noinst_HEADERS = \
    ignite/odbc.h \
    ignite/odbc/query/table_metadata_query.h \
    ignite/odbc/query/special_columns_query.h \
    ignite/odbc/query/streaming_query.h \
    ignite/odbc/query/type_info_query.h \
    ignite/odbc/query/batch_query.h \
    ignite/odbc/query/data_query.h \
    ignite/odbc/query/foreign_keys_query.h \
    ignite/odbc/query/internal_query.h \
    ignite/odbc/query/column_metadata_query.h \
    ignite/odbc/query/query.h \
    ignite/odbc/query/primary_keys_query.h \
    ignite/odbc/protocol_version.h \
    ignite/odbc/statement.h \
    ignite/odbc/config/configuration.h \
    ignite/odbc/config/connection_info.h \
    ignite/odbc/column.h \
    ignite/odbc/parser.h \
    ignite/odbc/app/application_data_buffer.h \
    ignite/odbc/app/parameter.h \
    ignite/odbc/app/parameter_set.h \
    ignite/odbc/row.h \
    ignite/odbc/utility.h \
    ignite/odbc/environment.h \
    ignite/odbc/dsn_config.h \
    ignite/odbc/system/odbc_constants.h \
    ignite/odbc/meta/primary_key_meta.h \
    ignite/odbc/meta/column_meta.h \
    ignite/odbc/meta/table_meta.h \
    ignite/odbc/diagnostic/diagnostic_record.h \
    ignite/odbc/diagnostic/diagnostic_record_storage.h \
    ignite/odbc/diagnostic/diagnosable.h \
    ignite/odbc/diagnostic/diagnosable_adapter.h \
    ignite/odbc/sql/sql_command.h \
    ignite/odbc/sql/sql_lexer.h \
    ignite/odbc/sql/sql_parser.h \
    ignite/odbc/sql/sql_set_streaming_command.h \
    ignite/odbc/sql/sql_token.h \
    ignite/odbc/sql/sql_utils.h \
    ignite/odbc/streaming/streaming_batch.h \
    ignite/odbc/streaming/streaming_context.h \
    ignite/odbc/connection.h \
    ignite/odbc/odbc_error.h \
    ignite/odbc/ssl_mode.h \
    ignite/odbc/message.h \
    ignite/odbc/cursor.h \
    ignite/odbc/common_types.h \
    ignite/odbc/result_page.h \
    ignite/odbc/type_traits.h \
    ignite/odbc/log.h

uninstall-hook:
	if [ -d ${includedir}/ignite ]; then find ${includedir}/ignite -type d -empty -delete; fi
