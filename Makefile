PROTO_DIR := src/main/proto
GEN_DIR := src/main/java/com/project/order_service/grpcGen
PLUGIN_PATH := /usr/local/bin/protoc-gen-grpc-java

.PHONY: gen clean check-protoc

gen: check-protoc
	protoc --plugin=protoc-gen-grpc-java=$(PLUGIN_PATH) \
		--proto_path=$(PROTO_DIR) \
		--java_out=$(GEN_DIR) \
		--grpc-java_out=$(GEN_DIR) \
		$(PROTO_DIR)/*.proto

clean:
	rm -rf $(GEN_DIR)/com/project/order_service/gen

check-protoc:
	@which protoc > /dev/null || (echo "protoc is not installed. Please install it."; exit 1)
	@test -f $(PLUGIN_PATH) || (echo "protoc-gen-grpc-java plugin is not found at $(PLUGIN_PATH). Please check the path."; exit 1)



